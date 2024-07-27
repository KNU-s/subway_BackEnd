package com.knu.subway.webSocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knu.subway.entity.Subway;
import com.knu.subway.service.ApiService;
import com.knu.subway.service.StationInfoService;
import com.knu.subway.service.SubwayService;
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
    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
    //메세지를 수신했을 때 실행
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        String receivedMessage = message.getPayload();
        sessionStationMap.remove(session);
        synchronized (sessionStationMap) {
            sessionStationMap.put(session, receivedMessage);
            sendSubwayData();
        }
    }
    //연결 됐을 때 실행
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessionMap.put(session.getId(), session);
        session.sendMessage(new TextMessage(session.getId()));
    }
    //연결이 종료 됐을 때 실행
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
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
                    sendData(session, message);
                }
            });
        }
    }

    public void sendData(WebSocketSession session, String message) {
        try {
            List<Subway> subwayList = subwayService.findByStationLine(message);
            if (!subwayList.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                String json = objectMapper.writeValueAsString(subwayList);
                session.sendMessage(new TextMessage(json));
            } else {
                session.sendMessage(new TextMessage("해당 호선은 데이터가 없습니다. 다시 확인해주세요."));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
