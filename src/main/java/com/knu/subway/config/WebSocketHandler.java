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
import org.springframework.boot.json.JsonParseException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
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
    List<String> stationList = new ArrayList<>();
    Map<String, WebSocketSession> sessionMap = new HashMap<>(); /*웹소켓 세션을 담아둘 맵*/
    /* 클라이언트로부터 메시지 수신시 동작 */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String stationName = message.getPayload(); /*stockCode <- 클라이언트에서 입력한 message*/
        log.info("===============Message=================");
        log.info("Received StationName : {}", stationName);
        log.info("===============Message=================");
        stationList = subwayInfoService.findAll().stream()
                .map(SubwayInfo::getSubwayName)
                .collect(Collectors.toList());

        synchronized (sessionMap) {
            sessionStationMap.put(session, stationList);
        }
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
                List<String> messages = sessionStationMap.get(session);
                for(String message : messages){
                    if(message!=null) {
                        try { // 지하철 데이터를 가져오는 로직이 길어 service 단에 설계
                            List<Dto> api = apiService.getSubwayArrivals(message);
                            for(Dto dto : api){
                                List<Subway> byStatnId = subwayService.findByStatnId(dto.getStatnId());
                                boolean exists = byStatnId.stream()
                                        .map(Subway::getStatnId)
                                        .anyMatch(statnId -> statnId.equals(dto.getStatnId()));

                                if (!exists) {
                                    subwayService.save(dto.toEntity());
                                } else {
                                    subwayService.update(byStatnId.get(0).getId(), dto);
                                }
                            }
                            if (api != null) {
                                log.info("Sending station data : {}", api);
                            } else {
                                log.warn("No station data found for stationCode : {}", message);
                            }
                        } catch (Exception e) {
                            log.error("Error while sending station data : {}", e.getMessage());
                        }
                    }
                }
            }

        }
    }

}