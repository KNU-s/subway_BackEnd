package com.knu.subway.service;

import com.knu.subway.entity.UserVisitLog;
import com.knu.subway.repository.UserVisitLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class UserVisitService {
    private final UserVisitLogRepository userVisitLogRepository;

    public boolean existsById(String id){
        return userVisitLogRepository.existsById(id);
    }
    public UserVisitLog findById(String id) {
        return userVisitLogRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Not Found UserVisit Data"));
    }
    public UserVisitLog findByIp(String ip) {
        return userVisitLogRepository.findByIp(ip);
    }

    // 기존 방문자 여부 확인 및 로그 업데이트
    public void connect(String sessionId, String ip) {
        UserVisitLog visitLog = findByIp(ip);
        // IPv6 localhost를 IPv4로 변환
        if (visitLog != null) {
            visitLog.incrementVisitCount(); // 방문 횟수 증가
            visitLog.updateStatus("Connected"); // 상태 업데이트
            visitLog.setUpdated(LocalDateTime.now().plusHours(9));
        } else {
            visitLog = UserVisitLog.builder()
                    .sessionId(sessionId)
                    .ip(ip)
                    .created(LocalDateTime.now().plusHours(9))
                    .updated(LocalDateTime.now().plusHours(9))
                    .status("Connected")
                    .count(1)
                    .build();
        }
        userVisitLogRepository.save(visitLog);
    }

    public void closed(String sessionId, String ip) {
        UserVisitLog visitLog = findByIp(ip);
        // IPv6 localhost를 IPv4로 변환
        if (visitLog != null) {
            visitLog.updateStatus("Closed"); // 상태 업데이트
            visitLog.setUpdated(LocalDateTime.now().plusHours(9));
        }
        userVisitLogRepository.save(visitLog);
    }
}
