package com.knu.subway.repository;

import com.knu.subway.entity.UserVisitLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserVisitLogRepository extends MongoRepository<UserVisitLog, String> {
    UserVisitLog findByIp(String ip);
}
