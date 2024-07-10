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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
@RequiredArgsConstructor
@Service
public class ApiService {

    private WebClient webClient;
    private final StationInfoService stationInfoService;
    private HashMap<String, String> stationNameHashMap;
    private List<StationInfo> infoList;


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
            stationNameHashMap.put(data.getStationId(), data.getStationName());
        }
    }

    public List<SubwayDTO> getSubwayArrivals(String stationName) {
        System.out.println(stationNameHashMap);
        Mono<String> response = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/{stationName}").build(stationName))
                .retrieve()
                .bodyToMono(String.class);

        String responseBody = response.block();

        if (responseBody == null || responseBody.isEmpty()) {
            return Collections.emptyList();
        }

        return parseResponse(responseBody, stationNameHashMap);
    }

    private List<SubwayDTO> parseResponse(String responseBody, HashMap<String, String> stationNameHashMap) {
        List<SubwayDTO> dtos = new ArrayList<>();

        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(responseBody);
            JSONArray element = (JSONArray) jsonObject.get("realtimeArrivalList");

            for (Object o : element) {
                JSONObject tempEle = (JSONObject) o;
                SubwayDTO dto = new SubwayDTO();
                dto.setStatnId(stationNameHashMap.get((String) tempEle.get("statnId")));
                dto.setPrevId(stationNameHashMap.get((String) tempEle.get("statnFid")));
                dto.setNextId(stationNameHashMap.get((String) tempEle.get("statnTid")));
                dto.setDstStation((String) tempEle.get("trainLineNm"));
                dto.setDstTime((String) tempEle.get("barvlDt"));
                dto.setDstMessage1((String) tempEle.get("arvlMsg2"));
                dto.setDstMessage2((String) tempEle.get("arvlMsg3"));
                dto.setTrainStatus((String) tempEle.get("arvlCd"));
                dto.setUpdnLine((String) tempEle.get("updnLine"));
                dto.setSubwayLine((String) tempEle.get("subwayId"));
                dtos.add(dto);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return dtos;
    }
}
