package com.knu.subway.service;

import com.knu.subway.entity.StationInfo;
import com.knu.subway.entity.dto.SubwayDTO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private HashMap<String, String[]> stationNameHashMap;
    private List<StationInfo> infoList;
    private static final Logger log = LoggerFactory.getLogger(ApiService.class);

    @Value("${subway.api.key}")
    private String apiKey;

    @Value("${subway.api.url}")
    private String baseUrl;
    @PostConstruct
    private void init() {
        String fullUrl = baseUrl + apiKey + "/json/realtimeStationArrival/0/5";
        this.webClient = WebClient.create(fullUrl);
        this.infoList = stationInfoService.findAll();
        this.stationNameHashMap = new HashMap<>();
        for(StationInfo data : infoList){
            stationNameHashMap.put(data.getStationId(), new String[]{data.getStationName(),data.getStationLine()});
        }

    }

    public List<SubwayDTO> getSubwayArrivals(String stationName) {
        Mono<String> response = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/{stationName}").build(stationName))
                .retrieve()
                .bodyToMono(String.class);

        String responseBody = response.block();

        if (responseBody == null || responseBody.isEmpty()) {
            return Collections.emptyList();
        }

        return parseResponse(responseBody);
    }

    private List<SubwayDTO> parseResponse(String responseBody) {
        List<SubwayDTO> subwayDTOList = new ArrayList<>();

        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(responseBody);
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
                        log.warn("No information found for statnId: {}", statnId);
                    }
                    if (statnFidInfo == null) {
                        log.warn("No information found for statnFid: {}", statnFid);
                    }
                    if (statnTidInfo == null) {
                        log.warn("No information found for statnTid: {}", statnTid);
                    }

                    // Set defaults if stationNameHashMap.get returns null
                    subwayDTO.setStatnNm(stationIdInfo != null ? stationIdInfo[0] : "Unknown");
                    subwayDTO.setStatnFNm(statnFidInfo != null ? statnFidInfo[0] : "Unknown");
                    subwayDTO.setStatnTNm(statnTidInfo != null ? statnTidInfo[0] : "Unknown");

                    subwayDTO.setBstatnNm((String) tempEle.get("bstatnNm"));

                    subwayDTO.setArvlMsg((String) tempEle.get("arvlMsg2"));
                    subwayDTO.setArvlStatus((String) tempEle.get("arvlCd"));
                    subwayDTO.setUpdnLine((String) tempEle.get("updnLine"));

                    // Set subway line, with default if stationNameHashMap.get returns null
                    subwayDTO.setSubwayLine(stationIdInfo != null ? stationIdInfo[1] : "Unknown");

                    subwayDTO.setBtrainNo((String) tempEle.get("btrainNo"));
                    subwayDTO.setBtrainSttus((String) tempEle.get("btrainSttus"));
                    subwayDTO.setLstcarAt(Objects.equals((String)tempEle.get("lstcarAt"),"0"));
                    subwayDTOList.add(subwayDTO);
                }
            }
        } catch (ParseException e) {
            log.error("Error while parsing JSON response: {}", e.getMessage(), e);
        }

        return subwayDTOList;
    }

}
