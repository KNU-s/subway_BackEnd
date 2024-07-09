package com.knu.subway.repository;

import com.knu.subway.entity.SubwayInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SubwayInfoRepository extends MongoRepository<SubwayInfo, String> {
    List<SubwayInfo> findBySubwayName(String subwayName);
}
