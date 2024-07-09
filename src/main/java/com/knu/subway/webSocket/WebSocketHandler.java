package com.knu.subway.webSocket;

import com.knu.subway.entity.SubwayInfo;
import com.knu.subway.entity.dto.SubwayDTO;
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

@Getter
@RequiredArgsConstructor
@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(WebSocketHandler.class);
    private final Map<WebSocketSession, String> sessionStationMap = new ConcurrentHashMap<>();
    private final ApiService apiService;
    private final SubwayInfoService subwayInfoService;
    private final SubwayService subwayService;
    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String receivedMessage = message.getPayload();
        log.info("Received Message: {}", receivedMessage);

        synchronized (sessionStationMap) {
            sessionStationMap.put(session, receivedMessage);
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
        synchronized (sessionStationMap) {
            sessionStationMap.remove(session);
        }
    }

    @Scheduled(fixedRate = 5000)
    public void sendSubwayData() {
        synchronized (sessionMap) {
            sessionMap.values().forEach(session -> {
                System.out.println("TEST1");
                String station = sessionStationMap.get(session);
                if (station != null && !station.isEmpty()) {
                    sendStationData(session, station);
                }
            });
        }
    }

    private void sendStationData(WebSocketSession session, String station) {
        List<SubwayInfo> subwayInfos = subwayInfoService.findByStationName(station);
        System.out.println("TEST2");
        System.out.println(subwayInfos);
        if (!subwayInfos.isEmpty()) {
            System.out.println("TEST3");
            try {
                List<SubwayDTO> data = apiService.getSubwayArrivals(station);
                log.info("Sending station data for station {}: {}", station, data);
                session.sendMessage(new TextMessage(data.toString()));
                // Add logic to send data to the WebSocket session if needed
                // session.sendMessage(new TextMessage(...));
            } catch (Exception e) {
                log.error("Error while sending station data for station {}: {}", station, e.getMessage(), e);
            }
        }
    }
}
