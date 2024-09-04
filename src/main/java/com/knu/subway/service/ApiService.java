package com.knu.subway.service;

import com.knu.subway.entity.StationInfo;
import com.knu.subway.entity.dto.SubwayDTO;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
@RequiredArgsConstructor
@Service
public class ApiService {

    private WebClient webClient;
    private final StationInfoService stationInfoService;
    private Map<String, Object[]> stationNameHashMap;
    private List<StationInfo> infoList;
    private List<String> station_2;
    @Value("${subway.api.key}")
    private String apiKey;

    @Value("${subway.api.url}")
    private String baseUrl;

    private static long count = 0;

    @Scheduled(cron = "0 */10 * * * *")
    private void check(){
        System.out.println("호출 횟수 = "+ count);
    }
    @PostConstruct
    private void init() {
        String fullUrl = baseUrl + apiKey + "/json/realtimeStationArrival/0/10";
        this.webClient = WebClient.create(fullUrl);
        this.infoList = stationInfoService.findAll();

        this.stationNameHashMap = new HashMap<>();
        for (StationInfo data : infoList) {
            Object[] stationData = stationNameHashMap.getOrDefault(data.getStationId(),
                    new Object[]{data.getStationName(), data.getStationLine(), 0});
            stationData[2] = (int) stationData[2] + 1;
            stationNameHashMap.put(data.getStationId(), stationData);
            String newStationId = String.valueOf(Integer.parseInt(data.getStationId()) + 10000);
            stationNameHashMap.put(newStationId,
                    new Object[]{data.getStationName(), data.getStationLine(), stationData[2]});
        }
    }

    public List<SubwayDTO> getSubwayArrivals(String stationName) {
        count++;
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/{stationName}").build(stationName))
                .retrieve()
                .bodyToMono(String.class)
                .retry(2)
                .onErrorResume(throwable -> {
                    throw new RuntimeException("Error occurred while fetching subway arrivals: " + throwable.getMessage());
                })
                .map(responseBody -> parseResponse(responseBody, stationName))
                .block();
    }

    private List<SubwayDTO> parseResponse(String responseBody, String station) {
        List<SubwayDTO> subwayDTOList = new ArrayList<>();

        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(responseBody);

            if (isErrorResponse(jsonObject)) {
                throw new RuntimeException("Error in response: " + getErrorMessage(jsonObject, station));
            }

            JSONArray element = (JSONArray) jsonObject.get("realtimeArrivalList");
            if (element != null) {
                for (Object o : element) {
                    parseSubwayDTO(subwayDTOList, (JSONObject) o);
                }
            }
        } catch (ParseException e) {
            throw new RuntimeException("Error while parsing JSON response: " + e.getMessage(), e);
        }
        return subwayDTOList;
    }

    private boolean isErrorResponse(JSONObject jsonObject) {
        return jsonObject.containsKey("status") && (Long) jsonObject.get("status") != 200;
    }

    private String getErrorMessage(JSONObject jsonObject, String station) {
        return String.format("status=%s, code=%s, message=%s, station=%s",
                jsonObject.get("status"),
                jsonObject.get("code"),
                jsonObject.get("message"),
                station);
    }

    private void parseSubwayDTO(List<SubwayDTO> subwayDTOList, JSONObject tempEle) {
        String statnId = (String) tempEle.get("statnId");
        Object[] stationIdInfo = stationNameHashMap.get(statnId);

        if (stationIdInfo == null) {
            return;
        }
        String arvlMsg2 = (String) tempEle.get("arvlMsg2");
        if (arvlMsg2.matches(".*\\d.*")) {
            return;
        }
        SubwayDTO subwayDTO = SubwayDTO.builder()
                .statnNm(cleanStationName((String) tempEle.get("arvlMsg3")))
                .statnFNm(getStationName((String) tempEle.get("statnFid")))
                .statnTNm(getStationName((String) tempEle.get("statnTid")))
                .bstatnNm((String) tempEle.get("bstatnNm"))
                .arvlMsg((String) tempEle.get("arvlMsg2"))
                .arvlStatus((String) tempEle.get("arvlCd"))
                .updnLine((String) tempEle.get("updnLine"))
                .subwayLine((String) stationIdInfo[1])
                .btrainNo((String) tempEle.get("btrainNo"))
                .btrainSttus((String) tempEle.get("btrainSttus"))
                .lstcarAt(Objects.equals(tempEle.get("lstcarAt").toString(), "1"))
                .created(LocalDateTime.now().plusHours(9))
                .updated(LocalDateTime.now().plusHours(9))
                .build();

        subwayDTOList.add(subwayDTO);

        if ((int) stationIdInfo[2] > 1 && stationNameHashMap.containsKey(String.valueOf(Integer.parseInt(statnId) + 10000))) {
            SubwayDTO clone = subwayDTO.toBuilder()
                    .subwayLine((String) stationNameHashMap.get(String.valueOf(Integer.parseInt(statnId) + 10000))[1])
                    .build();
            subwayDTOList.add(clone);
        }
    }

    private String cleanStationName(String arvlMsg3) {
        if (arvlMsg3 != null && arvlMsg3.endsWith("역")) {
            return arvlMsg3.substring(0, arvlMsg3.length() - 1);
        }
        return arvlMsg3;
    }

    private String getStationName(String statnId) {
        Object[] stationInfo = stationNameHashMap.get(statnId);
        return stationInfo != null ? stationInfo[0].toString() : "Unknown";
    }

    private String getFirstWord(String text) {
        return text != null ? text.split(" ")[0] : "Unknown";
    }
}
