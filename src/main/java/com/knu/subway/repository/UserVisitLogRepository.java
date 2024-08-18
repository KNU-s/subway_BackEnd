package com.knu.subway.repository;

import com.knu.subway.entity.UserVisitLog;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserVisitLogRepository extends MongoRepository<UserVisitLog, String> {
    Optional<UserVisitLog> findByIp(String ip);
}
