package com.knu.subway.repository;

import com.knu.subway.entity.Subway;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SubwayRepository extends MongoRepository<Subway, String> {
    List<Subway> findByStatnNm(String statnNm);
    List<Subway> findByBtrainNo(String BtrainNo);
    List<Subway> findBySubwayLine(String subwayLine);
}
