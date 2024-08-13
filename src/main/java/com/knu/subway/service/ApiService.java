package com.knu.subway.service;

import com.knu.subway.entity.StationInfo;
import com.knu.subway.entity.dto.SubwayDTO;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
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

        //임시 데이터
        station_2 = List.of("성수", "건대입구" , "구의", "강변", "잠실나루", "잠실", "잠실새내", "종합운동장", "삼성", "선릉", "역삼", "강남", "교대"
                , "서초", "방배", "사당", "낙성대", "서울대입구", "봉천", "신림", "신대방", "구로디지털단지", "대림", "신도림", "문래", "영등포구청", "당산", "합정"
                , "홍대입구", "신촌", "이대", "아현", "충정로", "시청", "을지로입구", "을지로3가", "을지로4가", "동대문역사문화공원", "신당", "상왕십리", "왕십리",
                "한양대", "뚝섬");
    }

    public List<SubwayDTO> getSubwayArrivals(String stationName) {
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
        //2호선 데이터가 불안정해서 임시로 넣은 로직 !! 삭제 예정
        boolean station2 = false;
        int curIndex = 0;
        if (stationIdInfo[1].toString().contains("2호선-(내선순환)") && cleanStationName((String)tempEle.get("arvlMsg3")).equals(getStationName((String)tempEle.get("statnFid")))) {
            if (station_2.contains(cleanStationName((String)tempEle.get("arvlMsg3")))) {
                station2 = true;
                curIndex = station_2.indexOf(cleanStationName((String)tempEle.get("arvlMsg3")));
                if(((String)tempEle.get("updnLine")).equals("내선")){
                    curIndex += 1;
                } else if(((String)tempEle.get("updnLine")).equals("외선")){
                    curIndex -= 1;
                }
            }

        }

        SubwayDTO subwayDTO = SubwayDTO.builder()
                .statnNm(station2 ? station_2.get(curIndex) : cleanStationName((String) tempEle.get("arvlMsg3")))
                .statnFNm(getStationName((String) tempEle.get("statnFid")))
                .statnTNm(getStationName((String) tempEle.get("statnTid")))
                .bstatnNm(getFirstWord((String) tempEle.get("bstatnNm")))
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
