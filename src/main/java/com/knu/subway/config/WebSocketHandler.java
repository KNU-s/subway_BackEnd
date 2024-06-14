package com.knu.subway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knu.subway.Dto;
import com.knu.subway.service.ApiService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.json.JsonParseException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@RequiredArgsConstructor
@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private Logger log = LoggerFactory.getLogger(WebSocketHandler.class);
    private final Map<WebSocketSession, String> sessionStockCodeMap = new ConcurrentHashMap<>();
    private final ApiService apiService;
    Map<String, WebSocketSession> sessionMap = new HashMap<>(); /*웹소켓 세션을 담아둘 맵*/
    /* 클라이언트로부터 메시지 수신시 동작 */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String stationName = message.getPayload(); /*stockCode <- 클라이언트에서 입력한 message*/
        log.info("===============Message=================");
        log.info("Received StationName : {}", stationName);
        log.info("===============Message=================");
        synchronized (sessionMap) {
            sessionStockCodeMap.put(session, stationName);
        }
        log.info("session Count : {}", sessionMap.size());
        log.info("session Count : {}", sessionStockCodeMap.size());
    }

    /* 클라이언트가 소켓 연결시 동작 */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Web Socket Connected");
        log.info("session id : {}", session.getId());
        super.afterConnectionEstablished(session);
        synchronized (sessionMap) { // 여러 클라이언트의 동시 접근하여 Map의 SessionID가 변경되는 것을 막기위해
            sessionMap.put(session.getId(), session);
        }
        System.out.println("sessionMap :" + sessionMap.toString());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sessionId", session.getId());

        session.sendMessage(new TextMessage(jsonObject.toString()));
    }

    /* 클라이언트가 소켓 종료시 동작 */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("Web Socket DisConnected");
        log.info("session id : {}", session.getId());
        synchronized (sessionMap) { // 여러 클라이언트의 동시 접근하여 Map의 SessionID가 변경되는 것을 막기위해
            sessionMap.remove(session.getId());
        }
        super.afterConnectionClosed(session, status); /*실제로 closed*/
    }
    @Scheduled(fixedRate = 5000)
    public void sendStockCode() throws JsonParseException {
        synchronized (sessionMap){
            for (WebSocketSession session : sessionMap.values()){
                String stationName = sessionStockCodeMap.get(session);
                if(stationName!=null) {
                    try { // 주식 데이터를 가져오는 로직이 길어 service 단에 설계
                        List<Dto> api = apiService.getSubwayArrivals(stationName);
                        if (api != null) {

                            log.info("Sending stock data : {}", api);
                        } else {
                            log.warn("No stock data found for stockCode : {}", stationName);
                        }
                    } catch (Exception e) {
                        log.error("Error while sending stock data : {}", e.getMessage());
                    }
                }
            }
        }
    }

}