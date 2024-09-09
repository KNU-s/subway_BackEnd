package com.knu.subway.webSocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.knu.subway.entity.Subway;
import com.knu.subway.service.ApiService;
import com.knu.subway.service.StationInfoService;
import com.knu.subway.service.SubwayService;
import com.knu.subway.service.UserVisitService;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

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
        String stationName = message.getPayload();  // 프론트에서 보낸 역 이름을 수신
        //노선 요청은 노선_노선명 이런 형태로 넘어온다.
        if(stationName != null && stationName.contains("_") && stationName.split("_")[0].equals("노선")) {
            sendData(session, stationName.split("_")[1]);  // 해당 역의 지하철 데이터를 전송
        }
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

    // 특정 역의 지하철 데이터를 전송
    public void sendData(WebSocketSession session, String stationName) {
        try {
            List<Subway> subwayList = subwayService.findBySubwayLine(stationName);
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
