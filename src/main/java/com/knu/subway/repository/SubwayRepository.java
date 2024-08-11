package com.knu.subway.repository;

import com.knu.subway.entity.Subway;
import java.time.LocalDateTime;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubwayRepository extends MongoRepository<Subway, String> {
    List<Subway> findByStatnNm(String statnNm);
    List<Subway> findByBtrainNo(String btrainNo);
    List<Subway> findBySubwayLine(String subwayLine);
    List<Subway> findByBtrainNoAndSubwayLine(String btrainNo, String subwayLine);
    List<Subway> findByUpdatedIsBefore(LocalDateTime time);

}
