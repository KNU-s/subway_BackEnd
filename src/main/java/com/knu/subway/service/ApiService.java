package com.knu.subway.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knu.subway.Dto;
import jakarta.annotation.PostConstruct;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class ApiService {

    private WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${subway.api.key}")
    private String apiKey;

    @Value("${subway.api.url}")
    private String baseurl;

    public ApiService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    private void init() {
        String baseUrl = baseurl + apiKey + "/json/realtimeStationArrival/0/3";
        System.out.println("TEST");
        System.out.println(apiKey);
        System.out.println(baseurl);

        this.webClient = WebClient.create(baseUrl);
    }

    public List<Dto> getSubwayArrivals(String stationName) {
        Mono<String> response = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/{stationName}").build(stationName))
                .retrieve()
                .bodyToMono(String.class);

        String responseBody = response.block();
        System.out.println(responseBody);
        var dtos = new ArrayList<Dto>();
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(responseBody);
            JSONArray element = (JSONArray) jsonObject.get("realtimeArrivalList");
            for (int i = 0; i < element.size(); i++) {
                var tempEle = (JSONObject) element.get(i);
                var dto = new Dto();
                dto.setStatnId((String) tempEle.get("statnId"));
//                dto.setPrevId((Long) tempEle.get("statnFid"));
//                dto.setPrevId((Long) tempEle.get("statnTid"));
//                dto.setTransferStations(Arrays.stream(((String)tempEle.get("tmsitCo")).split(",")).toList());
                dto.setDstStation((String) tempEle.get("trainLineNm"));
//                dto.setDstTime((Long) tempEle.get("barvlDt"));
                dto.setDstMessage1((String) tempEle.get("arvlMsg2"));
                dto.setDstMessage2((String) tempEle.get("arvlMsg3"));
//                dto.setTrainStatus(TrainStatus.fromCode((int)tempEle.get("arvlCd")));
                dto.setUpdnLine((String) tempEle.get("updnLine"));
//                dto.setSubwayLine(SubwayLine.fromCode((int)tempEle.get("subwayId")));

//                dto.setTrainLineNm((String) tempEle.get("trainLineNm"));
//                dto.setStatnNm((String) tempEle.get("statnNm"));
//                dto.setBarvlDt((String) tempEle.get("trainLineNm"));
//                dto.setBtrainNo((String) tempEle.get("btrainNo"));
//                dto.setBstatnId((String) tempEle.get("bstatnId"));
//                dto.setBstatnNm((String) tempEle.get("bstatnNm"));
//                dto.setRecptnDt((String) tempEle.get("recptnDt"));
//                dto.setArvlMsg2((String) tempEle.get("arvlMsg2"));
//                dto.setArvlMsg3((String) tempEle.get("arvlMsg3"));
//                dto.setArvlCd((String) tempEle.get("arvlCd"));
                dtos.add(dto);
            }

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        System.out.println(dtos);
        if (responseBody == null || responseBody.isEmpty()) {
            return Collections.emptyList(); // or handle as needed
        }

        return dtos;
    }

    private List<Dto> parseResponse(String responseBody) {
        try {
            // Parse JSON into a single Dto object (assuming JSON starts with an object, not array)
            Dto dto = objectMapper.readValue(responseBody, Dto.class);
            return Collections.singletonList(dto);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error parsing JSON response", e);
        }
    }
}
