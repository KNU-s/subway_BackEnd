package com.knu.subway.webSocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.knu.subway.entity.Subway;
import com.knu.subway.service.ApiService;
import com.knu.subway.service.StationInfoService;
import com.knu.subway.service.SubwayService;
import com.knu.subway.service.UserVisitService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Getter
@RequiredArgsConstructor
@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private final Map<WebSocketSession, String> sessionStationMap = new ConcurrentHashMap<>();
    private final ApiService apiService;
    private final StationInfoService stationInfoService;
    private final SubwayService subwayService;
    private final UserVisitService userVisitService;
    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    // 메시지를 수신했을 때 실행
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        String receivedMessage = message.getPayload();
        synchronized (sessionStationMap) {
            sessionStationMap.put(session, receivedMessage);
        }
        sendSubwayData();
    }

    // 연결됐을 때 실행
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String ipAddress = (String) session.getAttributes().get("ipAddress");
        if (ipAddress != null && !ipAddress.equals("127.0.0.1") && !ipAddress.equals("0:0:0:0:0:0:0:1")) {
            userVisitService.connect(session.getId(), ipAddress);
        }
        sessionMap.put(session.getId(), session);
        session.sendMessage(new TextMessage(session.getId()));
    }

    // 연결이 종료됐을 때 실행
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionMap.remove(session.getId());
        synchronized (sessionStationMap) {
            sessionStationMap.remove(session);
        }
        String ipAddress = (String) session.getAttributes().get("ipAddress");
        userVisitService.closed(session.getId(), ipAddress);
        log.info("WebSocket connection closed: Session ID = {}", session.getId());
    }

    // 5초마다 지하철 데이터를 전송
    @Scheduled(fixedRate = 5000)
    public void sendSubwayData() {
        synchronized (sessionMap) {
            sessionMap.values().removeIf(session -> !session.isOpen());  // 닫힌 세션 제거
            sessionMap.values().forEach(session -> {
                String message = sessionStationMap.get(session);
                if (message != null && !message.isEmpty()) {
                    sendData(session, message);
                }
            });
        }
    }

    public void sendData(WebSocketSession session, String message) {
        try {
            List<Subway> subwayList = subwayService.findBySubwayLine(message);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            String json = objectMapper.writeValueAsString(subwayList);
            session.sendMessage(new TextMessage(json));
        } catch (Exception e) {
            log.error("Failed to send data: {}", e.getMessage(), e);
            try {
                session.close(CloseStatus.SERVER_ERROR);
            } catch (Exception closeException) {
                log.error("Failed to close session after error: {}", closeException.getMessage(), closeException);
            }
        }
    }
}
