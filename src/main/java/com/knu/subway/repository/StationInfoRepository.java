package com.knu.subway.repository;

import com.knu.subway.entity.StationInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface StationInfoRepository extends MongoRepository<StationInfo, String> {
    List<StationInfo> findByStationName(String stationName);
    List<StationInfo> findByStationLine(String stationLine);
}
