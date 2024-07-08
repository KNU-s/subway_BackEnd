package com.knu.subway.service;

import com.knu.subway.Dto;
import com.knu.subway.entity.Subway;
import com.knu.subway.entity.subwayEnum.SubwayLine;
import com.knu.subway.entity.subwayEnum.TrainStatus;
import com.knu.subway.repository.SubwayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@RequiredArgsConstructor
@Service
public class SubwayService {
    private final SubwayRepository subwayRepository;
    public String save(Subway subway){

        subwayRepository.save(subway);
        return subway.getId();
    }
    public void update(String id, Dto subwayDto){
        Subway findSubway = findSubwayById_orElseThrow(id);
        findSubway.setNextId(subwayDto.getNextId());
        findSubway.setPrevId(subwayDto.getPrevId());
        findSubway.setDstTime(subwayDto.getDstTime());
        findSubway.setDstMessage1(subwayDto.getDstMessage1());
        findSubway.setDstMessage2(subwayDto.getDstMessage2());
        findSubway.setTrainStatus(TrainStatus.fromCode(subwayDto.getTrainStatus()));
        findSubway.setSubwayLine(SubwayLine.fromCode(subwayDto.getSubwayLine()));
        subwayRepository.save(findSubway);
    }

    public Subway findSubwayById_orElseThrow(String id){
        return subwayRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not Found SubwayById data."));
    }

    public List<Subway> findAll() {
        return subwayRepository.findAll();
    }

    public void deleteById(String id) {
        subwayRepository.deleteById(id);
        log.info("Delete Subway id :{}", id);
    }

    public List<Subway> findByStatnId(String statnId){
        return subwayRepository.findByStatnId(statnId);
    }
}
