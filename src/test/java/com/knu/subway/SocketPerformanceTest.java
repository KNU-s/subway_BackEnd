package com.knu.subway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SocketPerformanceTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private WebSocketClient webSocketClient;
    private String webSocketUrl;

    @BeforeEach
    public void setup() {
        webSocketClient = new ReactorNettyWebSocketClient();
        webSocketUrl = "ws://localhost:8090/kjs";
    }

    @Test
    public void testHttpApiPerformance() {
        String url = "/api/data?stationName=구로디지털단지";
        int iterations = 50;
        long totalDuration = 0;

        for (int i = 0; i < iterations; i++) {
            long startTime = System.currentTimeMillis();
            restTemplate.getForObject(url, Map.class);
            long endTime = System.currentTimeMillis();
            totalDuration += (endTime - startTime);
        }

        long averageDuration = totalDuration / iterations;
        System.out.println("Average HTTP API Duration: " + averageDuration + " ms");
    }

    @Test
    public void testWebSocketPerformance() throws InterruptedException {
        int iterations = 100;
        AtomicLong totalDuration = new AtomicLong(0);

        for (int i = 0; i < iterations; i++) {
            long startTime = System.currentTimeMillis();

            webSocketClient.execute(
                    URI.create(webSocketUrl),
                    session -> session.send(Mono.just(session.textMessage("구로디지털단지")))
                            .thenMany(session.receive()
                                    .take(1)
                                    .map(WebSocketMessage::getPayloadAsText)
                                    .doOnNext(message -> {
                                        long endTime = System.currentTimeMillis();
                                        totalDuration.addAndGet((endTime - startTime));
                                    }))
                            .then()
            ).block(Duration.ofSeconds(10));
        }

        long averageDuration = totalDuration.get() / iterations;
        System.out.println("Average WebSocket Duration: " + averageDuration + " ms");
    }
}