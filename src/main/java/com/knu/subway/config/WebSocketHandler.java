package com.knu.subway.config;

import com.knu.subway.Dto;
import com.knu.subway.entity.Subway;
import com.knu.subway.entity.SubwayInfo;
import com.knu.subway.service.ApiService;
import com.knu.subway.service.SubwayInfoService;
import com.knu.subway.service.SubwayService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private Logger log = LoggerFactory.getLogger(WebSocketHandler.class);
    private final Map<WebSocketSession, List<String>> sessionStationMap = new ConcurrentHashMap<>();
    private final ApiService apiService;
    private final SubwayInfoService subwayInfoService;
    private final SubwayService subwayService;
    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String receivedMessage = message.getPayload();
        log.info("Received Message: {}", receivedMessage);

        if ("start".equalsIgnoreCase(receivedMessage)) {
            List<SubwayInfo> subwayInfos = subwayInfoService.findAll();
            List<String> stationList = subwayInfos.stream()
                    .map(SubwayInfo::getSubwayName)
                    .collect(Collectors.toList());

            synchronized (sessionStationMap) {
                sessionStationMap.put(session, stationList);
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocket Connected");
        log.info("Session ID: {}", session.getId());
        sessionMap.put(session.getId(), session);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sessionId", session.getId());

        session.sendMessage(new TextMessage(jsonObject.toString()));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("WebSocket Disconnected");
        log.info("Session ID: {}", session.getId());
        sessionMap.remove(session.getId());
        sessionStationMap.remove(session);
    }

    @Scheduled(fixedRate = 5000)
    public void sendSubwayData() {
        synchronized (sessionMap) {
            for (WebSocketSession session : sessionMap.values()) {
                List<String> stationList = sessionStationMap.get(session);
                if (stationList != null) {
                    for (String stationName : stationList) {
                        try {
                            List<Dto> api = apiService.getSubwayArrivals(stationName);
                            for (Dto dto : api) {
                                List<Subway> existingSubways = subwayService.findByStatnId(dto.getStatnId());
                                if (existingSubways.isEmpty()) {
                                    subwayService.save(dto.toEntity());
                                } else {
                                    subwayService.update(existingSubways.get(0).getId(), dto);
                                }
                            }
                            log.info("Sending station data for station {}: {}", stationName, api);
                        } catch (Exception e) {
                            log.error("Error while sending station data for station {}: {}", stationName, e.getMessage());
                        }
                    }
                }
            }
        }
    }
}