package com.knu.subway.service;

import com.knu.subway.entity.StationInfo;
import com.knu.subway.repository.StationInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class StationInfoService {
    private final StationInfoRepository stationInfoRepository;
    public List<StationInfo> findAll() {
        return stationInfoRepository.findAll();
    }

    public Optional<StationInfo> findById(String id) {
        return stationInfoRepository.findById(id);
    }

    public StationInfo save(StationInfo stationInfo) {
        return stationInfoRepository.save(stationInfo);
    }

    public void deleteById(String id) {
        stationInfoRepository.deleteById(id);
    }

    public List<StationInfo> findByStationName(String stationName){
        return stationInfoRepository.findBySubwayName(stationName);
    }
}
