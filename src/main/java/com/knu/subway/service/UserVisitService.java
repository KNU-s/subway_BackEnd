package com.knu.subway.service;

import com.knu.subway.entity.UserVisitLog;
import com.knu.subway.repository.UserVisitLogRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserVisitService {
    private final UserVisitLogRepository userVisitLogRepository;
    private final Object lock = new Object();  // 동시성 제어를 위한 락 객체

    public boolean existsById(String id) {
        return userVisitLogRepository.existsById(id);
    }

    public UserVisitLog findById(String id) {
        return userVisitLogRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Not Found UserVisit Data"));
    }

    // 기존 방문자 여부 확인 및 로그 업데이트
    public void connect(String sessionId, String ip) {
        synchronized (getLock(ip)) {  // 특정 IP에 대해 동시성 제어
            Optional<UserVisitLog> optionalVisitLog = userVisitLogRepository.findByIp(ip);

            UserVisitLog visitLog;
            if (optionalVisitLog.isPresent()) {
                visitLog = optionalVisitLog.get();
                visitLog.setSessionId(sessionId);  // 동일 IP의 기존 레코드의 세션 ID를 업데이트
                visitLog.incrementVisitCount(); // 방문 횟수 증가
                visitLog.updateStatus("Connected"); // 상태 업데이트
            } else {
                visitLog = UserVisitLog.builder()
                        .sessionId(sessionId)
                        .ip(ip)
                        .status("Connected")
                        .count(1)
                        .created(LocalDateTime.now())
                        .updated(LocalDateTime.now())
                        .build();
            }
            visitLog.setUpdated(LocalDateTime.now());
            userVisitLogRepository.save(visitLog);
        }
    }

    public void closed(String sessionId, String ip) {
        synchronized (getLock(ip)) {  // 특정 IP에 대해 동시성 제어
            Optional<UserVisitLog> optionalVisitLog = userVisitLogRepository.findByIp(ip);

            if (optionalVisitLog.isPresent()) {
                UserVisitLog visitLog = optionalVisitLog.get();
                visitLog.updateStatus("Closed");
                visitLog.setUpdated(LocalDateTime.now());
                userVisitLogRepository.save(visitLog);
            }
        }
    }

    // IP별로 개별적인 락을 제공하기 위한 메서드
    private Object getLock(String ip) {
        // ip를 key로 하는 락 객체를 반환
        // (ConcurrentHashMap을 사용해 ip별로 다른 락 객체를 제공할 수 있습니다.)
        return lock;
    }
}
