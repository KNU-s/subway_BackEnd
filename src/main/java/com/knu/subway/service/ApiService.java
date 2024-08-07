package com.knu.subway.service;

import com.knu.subway.entity.StationInfo;
import com.knu.subway.entity.dto.SubwayDTO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
public class ApiService {

    private WebClient webClient;
    private final StationInfoService stationInfoService;
    private HashMap<String, String[]> stationNameHashMap;
    private List<StationInfo> infoList;

    @Value("${subway.api.key}")
    private String apiKey;

    @Value("${subway.api.url}")
    private String baseUrl;

    @PostConstruct
    private void init() {
        String fullUrl = baseUrl + apiKey + "/json/realtimeStationArrival/0/10";
        this.webClient = WebClient.create(fullUrl);
        this.infoList = stationInfoService.findAll();
        this.stationNameHashMap = new HashMap<>();
        for (StationInfo data : infoList) {
            stationNameHashMap.put(data.getStationId(), new String[]{data.getStationName(), data.getStationLine()});
        }
    }

    public List<SubwayDTO> getSubwayArrivals(String stationName) {
        Mono<String> response = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/{stationName}").build(stationName))
                .retrieve()
                .bodyToMono(String.class)
                .retry(2)
                .onErrorResume(throwable -> {
                    throw new RuntimeException("Error occurred while fetching subway arrivals: " + throwable.getMessage());
                });

        String responseBody = response.block();
        if (responseBody == null || responseBody.isEmpty()) {
            return Collections.emptyList();
        }

        return parseResponse(responseBody, stationName);
    }

    private List<SubwayDTO> parseResponse(String responseBody, String station) {
        List<SubwayDTO> subwayDTOList = new ArrayList<>();

        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(responseBody);

            // Check if there is an error in the response
            if (jsonObject.containsKey("status") && (Long) jsonObject.get("status") != 200) {
                String status = jsonObject.get("status").toString();
                String code = (String) jsonObject.get("code");
                String message = (String) jsonObject.get("message");
                throw new RuntimeException("Error in response: status=" + status + ", code=" + code + ", message=" + message + ", station=" + station);
            }

            JSONArray element = (JSONArray) jsonObject.get("realtimeArrivalList");
            if (element != null && !element.isEmpty()) {
                for (Object o : element) {
                    JSONObject tempEle = (JSONObject) o;
                    SubwayDTO subwayDTO = new SubwayDTO();

                    // Fetch values from stationNameHashMap with null safety
                    String statnId = (String) tempEle.get("statnId");
                    String statnFid = (String) tempEle.get("statnFid");
                    String statnTid = (String) tempEle.get("statnTid");

                    String[] stationIdInfo = stationNameHashMap.get(statnId);
                    String[] statnFidInfo = stationNameHashMap.get(statnFid);
                    String[] statnTidInfo = stationNameHashMap.get(statnTid);

                    if (stationIdInfo == null) {
                        break;
                    }
//                    if (statnFidInfo == null) {
//                        throw new RuntimeException("No information found for statnFid: " + statnFid);
//                    }
//                    if (statnTidInfo == null) {
//                        throw new RuntimeException("No information found for statnTid: " + statnTid);
//                    }

                    // Get the arrival message
                    String arvlMsg = (String) tempEle.get("arvlMsg2");

                    // Regular expression pattern to check for any digits in arvlMsg
                    Pattern digitPattern = Pattern.compile("\\d");

                    // If arvlMsg contains any digits, skip adding this subwayDTO to the list
                    if (arvlMsg != null && digitPattern.matcher(arvlMsg).find()) {
                        arvlMsg = (String) tempEle.get("arvlMsg3");
                    }

                    // Set defaults if stationNameHashMap.get returns null
                    subwayDTO.setStatnNm(stationIdInfo != null ? stationIdInfo[0] : "Unknown");
                    subwayDTO.setStatnFNm(statnFidInfo != null ? statnFidInfo[0] : "Unknown");
                    subwayDTO.setStatnTNm(statnTidInfo != null ? statnTidInfo[0] : "Unknown");

                    subwayDTO.setBstatnNm((String) tempEle.get("bstatnNm"));

                    subwayDTO.setArvlMsg(arvlMsg);
                    subwayDTO.setArvlStatus((String) tempEle.get("arvlCd"));
                    subwayDTO.setUpdnLine((String) tempEle.get("updnLine"));

                    // Set subway line, with default if stationNameHashMap.get returns null
                    subwayDTO.setSubwayLine(stationIdInfo != null ? stationIdInfo[1] : "Unknown");

                    subwayDTO.setBtrainNo((String) tempEle.get("btrainNo"));
                    subwayDTO.setBtrainSttus((String) tempEle.get("btrainSttus"));
                    subwayDTO.setLstcarAt(Objects.equals(tempEle.get("lstcarAt").toString(), "1"));
                    subwayDTOList.add(subwayDTO);
                }
            }
        } catch (ParseException e) {
            throw new RuntimeException("Error while parsing JSON response: " + e.getMessage(), e);
        }
        return subwayDTOList;
    }
}