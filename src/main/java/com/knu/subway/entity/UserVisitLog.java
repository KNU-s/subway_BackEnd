package com.knu.subway.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "visit_log")
public class UserVisitLog {
    @Id
    private String id;
    private String ip;
    private String sessionId;
    private LocalDateTime created;
    private LocalDateTime updated;
    private String status;
    private int count;

    @Builder
    public UserVisitLog(String id, String sessionId, String ip, LocalDateTime created, LocalDateTime updated, String status, int count) {
        this.id = id;
        this.ip = ip;
        this.sessionId = sessionId;
        this.created = created;
        this.updated = updated;
        this.status = status;
        this.count = count;
    }

    // 방문 횟수 증가
    public void incrementVisitCount() {
        this.count += 1;
    }

    // 상태 업데이트
    public void updateStatus(String newStatus) {
        this.status = newStatus;
        this.updated = LocalDateTime.now();
    }
}
