package com.knu.subway.webSocket;

import com.knu.subway.entity.Subway;
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
                    sendData(session, message);
                }
            });
        }
    }

    public void sendData(WebSocketSession session, String message) {
        StringBuilder sb = new StringBuilder();
        try {
            List<Subway> subwayList = subwayService.findByStationLine(message);
            if (!subwayList.isEmpty()) {
                for (Subway subway : subwayList) {
                    sb.append("{")
                            .append("\"trainId\":\"").append(subway.getTrainId()).append("\",")
                            .append("\"statnId\":\"").append(subway.getStatnId()).append("\",")
                            .append("\"prevStationName\":\"").append(subway.getPrevStationName()).append("\",")
                            .append("\"nextStationName\":\"").append(subway.getNextStationName()).append("\",")
                            .append("\"dstStation\":\"").append(subway.getDstStation()).append("\",")
                            .append("\"dstMessage1\":\"").append(subway.getDstMessage1()).append("\",")
                            .append("\"dstMessage2\":\"").append(subway.getDstMessage2()).append("\",")
                            .append("\"trainStatus\":\"").append(subway.getTrainStatus()).append("\",")
                            .append("\"updnLine\":\"").append(subway.getUpdnLine()).append("\",")
                            .append("\"subwayLine\":\"").append(subway.getSubwayLine()).append("\",")
                            .append("\"direction\":\"").append(subway.getDirection()).append("\",")
                            .append("}");
                }
                session.sendMessage(new TextMessage(sb.toString()));
            } else {
                log.warn("No valid station information found for message: {}", message);
            }
        } catch (Exception e) {
            log.error("Error while processing station data for message: {}", message, e);
        }
    }

}
