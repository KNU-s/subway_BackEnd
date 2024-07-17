package com.knu.subway.service;

import com.knu.subway.entity.Subway;
import com.knu.subway.entity.dto.SubwayDTO;
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
    public Subway update(Subway subway, SubwayDTO subwayDto){
        subway.setStatnId(subwayDto.getStatnId());
        subway.setNextStationName(subwayDto.getNextStationName());
        subway.setPrevStationName(subwayDto.getPrevStationName());
        subway.setDstTime(subwayDto.getDstTime());
        subway.setDstMessage1(subwayDto.getDstMessage1());
        subway.setDstMessage2(subwayDto.getDstMessage2());
        subway.setTrainStatus(TrainStatus.fromCode(subwayDto.getTrainStatus()).getDescription());
        return subway;
    }

    public Subway findSubwayById_orElseThrow(String id){
        return subwayRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not Found SubwayById data."));
    }

    public List<Subway> findAll() {
        return subwayRepository.findAll();
    }

    public void delete(Subway subway) {
        subwayRepository.delete(subway);
        log.info("Delete Subway :{}", subway.getStatnId());
    }
    public void deleteById(String id) {
        subwayRepository.deleteById(id);
        log.info("Delete Subway id :{}", id);
    }

    public List<Subway> findByStatnId(String statnId){
        return subwayRepository.findByStatnId(statnId);
    }

    public List<Subway> findByTrainId(String trainId){
        return subwayRepository.findByTrainId(trainId);
    }

    public List<Subway> findByStationLine(String stationLine) {
        return subwayRepository.findBySubwayLine(stationLine);
    }

    public void saveAll(List<Subway> subways){
        subwayRepository.saveAll(subways);
    }

}
