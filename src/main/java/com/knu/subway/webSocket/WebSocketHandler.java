package com.knu.subway.webSocket;

import com.knu.subway.entity.StationInfo;
import com.knu.subway.entity.dto.SubwayDTO;
import com.knu.subway.helper.JsonConverter;
import com.knu.subway.service.ApiService;
import com.knu.subway.service.StationInfoService;
import com.knu.subway.service.SubwayService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@RequiredArgsConstructor
@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(WebSocketHandler.class);
    private final Map<WebSocketSession, String> sessionStationMap = new ConcurrentHashMap<>();
    private final ApiService apiService;
    private final StationInfoService stationInfoService;
    private final SubwayService subwayService;
    private final JsonConverter jsonConverter;
    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
    //메세지를 수신했을 때 실행
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        String receivedMessage = message.getPayload();
        log.info("Received Message: {}", receivedMessage);
        synchronized (sessionStationMap) {
            sessionStationMap.put(session, receivedMessage);
        }
    }
    //연결 됐을 때 실행
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocket Connected");
        log.info("Session ID: {}", session.getId());
        sessionMap.put(session.getId(), session);
        session.sendMessage(new TextMessage(session.getId()));
    }
    //연결이 종료 됐을 때 실행
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
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
                String message = sessionStationMap.get(session);
                if (message != null && !message.isEmpty()) {
                    sendStationData(session, message);
                }
            });
        }
    }

    public void sendStationData(WebSocketSession session, String message) {
        try {
            Set<StationInfo> stationInfos = getValidStationInfos(message);

            if (!stationInfos.isEmpty()) {
                for (StationInfo info : stationInfos) {
                    sendSubwayArrivals(session, info.getStationName());
                }
            } else {
                log.warn("No valid station information found for message: {}", message);
            }
        } catch (Exception e) {
            log.error("Error while processing station data for message: {}", message, e);
        }
    }

    private Set<StationInfo> getValidStationInfos(String message) {
        List<StationInfo> stationInfos = stationInfoService.findByStationName(message);
        if (stationInfos == null || stationInfos.isEmpty()) {
            stationInfos = stationInfoService.findByStationLine(message);
        }
        return new HashSet<>(stationInfos);
    }

    private void sendSubwayArrivals(WebSocketSession session, String message) {
        try {
            List<SubwayDTO> data = apiService.getSubwayArrivals(message);
//            log.info("Sending station data for station {}: {}", message, data);
            List<String> jsonData = jsonConverter.convertToJsonList(data);
            String jsonString = jsonConverter.joinJsonStrings(jsonData);
            session.sendMessage(new TextMessage(jsonString));
        } catch (Exception e) {
            log.error("Error while sending subway arrivals for station {}: {}", message, e.getMessage(), e);
        }
    }
}
