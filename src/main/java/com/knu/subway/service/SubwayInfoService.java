package com.knu.subway.service;

import com.knu.subway.entity.SubwayInfo;
import com.knu.subway.repository.SubwayInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SubwayInfoService {
    private final SubwayInfoRepository subwayInfoRepository;

    public List<SubwayInfo> findAll(){
        return subwayInfoRepository.findAll();
    }
    public String save(SubwayInfo subwayInfo){
        SubwayInfo insert = subwayInfoRepository.save(subwayInfo);
        return insert.getId();
    }
}
