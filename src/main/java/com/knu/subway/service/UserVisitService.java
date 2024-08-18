package com.knu.subway.service;

import com.knu.subway.entity.UserVisitLog;
import com.knu.subway.repository.UserVisitLogRepository;
import java.util.Optional;
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

    // 기존 방문자 여부 확인 및 로그 업데이트
    public void connect(String sessionId, String ip) {
        Optional<UserVisitLog> optionalVisitLog = userVisitLogRepository.findByIp(ip); // Optional로 처리

        UserVisitLog visitLog;
        if (optionalVisitLog.isPresent()) {
            visitLog = optionalVisitLog.get();
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


    public void closed(String sessionId, String ip) {
        Optional<UserVisitLog> optionalVisitLog = userVisitLogRepository.findByIp(ip);

        if (optionalVisitLog.isPresent()) {
            UserVisitLog visitLog = optionalVisitLog.get();
            visitLog.updateStatus("Closed");
            visitLog.setUpdated(LocalDateTime.now());
            userVisitLogRepository.save(visitLog);
        }
    }

}
