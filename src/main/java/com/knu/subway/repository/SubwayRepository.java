package com.knu.subway.repository;

import com.knu.subway.entity.Subway;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SubwayRepository extends MongoRepository<Subway, String> {
    List<Subway> findByStatnId(String statnId);
    List<Subway> findByTrainId(String trainId);
    List<Subway> findBySubwayLine(String subwayLine);
}
