package com.knu.subway.service;

import com.knu.subway.entity.SubwayInfo;
import com.knu.subway.repository.SubwayInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SubwayInfoService {
    private final SubwayInfoRepository subwayInfoRepository;
    public List<SubwayInfo> findAll() {
        return subwayInfoRepository.findAll();
    }

    public Optional<SubwayInfo> findById(String id) {
        return subwayInfoRepository.findById(id);
    }

    public SubwayInfo save(SubwayInfo subwayInfo) {
        return subwayInfoRepository.save(subwayInfo);
    }

    public void deleteById(String id) {
        subwayInfoRepository.deleteById(id);
    }

    public List<SubwayInfo> findByStationName(String stationName){
        return subwayInfoRepository.findBySubwayName(stationName);
    }
}
