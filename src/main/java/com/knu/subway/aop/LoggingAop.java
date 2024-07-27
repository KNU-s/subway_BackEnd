package com.knu.subway.aop;

import com.knu.subway.entity.Subway;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Aspect
@Component
@Slf4j
public class LoggingAop {

    @Before("execution(* com.knu.subway.webSocket.WebSocketHandler.handleTextMessage(..)) && args(session, message)")
    public void logBeforeHandleTextMessage(WebSocketSession session, TextMessage message) {
        log.info("Received Message: {}", message.getPayload());
    }

    @AfterReturning("execution(* com.knu.subway.webSocket.WebSocketHandler.afterConnectionEstablished(..)) && args(session)")
    public void logAfterConnectionEstablished(WebSocketSession session) {
        log.info("WebSocket Connected");
        log.info("Session ID: {}", session.getId());
    }

    @AfterReturning("execution(* com.knu.subway.webSocket.WebSocketHandler.afterConnectionClosed(..)) && args(session, status)")
    public void logAfterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("WebSocket Disconnected");
        log.info("Session ID: {}", session.getId());
    }

    @AfterReturning(pointcut = "execution(* com.knu.subway.webSocket.WebSocketHandler.sendData(..)) && args(session, message)", returning = "result")
    public void logAfterSendingData(WebSocketSession session, String message, List<Subway> result) {
        for(Subway data : result) {
            System.out.println(data.getBtrainNo());
        }
    }

    @AfterThrowing(pointcut = "execution(* com.knu.subway.webSocket.WebSocketHandler.sendData(..)) && args(session, message)", throwing = "error")
    public void logAfterThrowingException(WebSocketSession session, String message, Throwable error) {
        log.error("Error while processing station data for message: {}", message, error);
    }
}
