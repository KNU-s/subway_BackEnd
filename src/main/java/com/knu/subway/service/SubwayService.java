package com.knu.subway.service;

import com.knu.subway.Dto;
import com.knu.subway.entity.Subway;
import com.knu.subway.entity.subwayEnum.SubwayLine;
import com.knu.subway.entity.subwayEnum.TrainStatus;
import com.knu.subway.repository.SubwayRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SubwayService {
    private final SubwayRepository subwayRepository;
    @Transactional
    public void save(Subway subway){
        subwayRepository.save(subway);
    }
    @Transactional
    public void update(String id, Dto subwayDto){
        Optional<Subway> subway = subwayRepository.findById(id);
        if(!subway.isPresent()){
            throw new IllegalArgumentException("존재하지 않는 열차입니다.");
        }
        Subway findSubway = subway.get();
        findSubway.setNextId(subwayDto.getNextId());
        findSubway.setPrevId(subwayDto.getPrevId());
        findSubway.setDstTime(subwayDto.getDstTime());
        findSubway.setDstMessage1(subwayDto.getDstMessage1());
        findSubway.setDstMessage2(subwayDto.getDstMessage2());
        findSubway.setTrainStatus(TrainStatus.fromCode(subwayDto.getTrainStatus()));
        findSubway.setSubwayLine(SubwayLine.fromCode(subwayDto.getSubwayLine()));
    }

    public List<Subway> findByStatnId(String statnId){
        return subwayRepository.findByStatnId(statnId);
    }
}
