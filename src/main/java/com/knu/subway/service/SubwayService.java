package com.knu.subway.service;

import com.knu.subway.entity.Subway;
import com.knu.subway.entity.dto.SubwayDTO;
import com.knu.subway.repository.SubwayRepository;
import java.time.LocalDateTime;
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
        subway.setStatnNm(subwayDto.getStatnNm());
        subway.setStatnTNm(subwayDto.getStatnTNm());
        subway.setStatnFNm(subwayDto.getStatnFNm());
        subway.setArvlMsg(subwayDto.getArvlMsg());
        subway.setUpdated(subwayDto.getUpdated());
        subway.setSubwayLine(subway.getSubwayLine());
        subway.setUpdnLine(subway.getUpdnLine());
//        subwayRepository.save(subway);
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
    }
    public void deleteById(String id) {
        subwayRepository.deleteById(id);
    }

    public List<Subway> findByStatnNm(String statnNm){
        return subwayRepository.findByStatnNm(statnNm);
    }

    public List<Subway> findByBtrainNo(String btrainNo){
        return subwayRepository.findByBtrainNo(btrainNo);
    }

    public List<Subway> findBySubwayLine(String subwayLine) {
        return subwayRepository.findBySubwayLine(subwayLine);
    }

    public void saveAll(List<Subway> subways){
        subwayRepository.saveAll(subways);
    }

    public void deleteAll(List<Subway> subways){
        subwayRepository.deleteAll(subways);
    }

    public void deleteAll(){
        subwayRepository.deleteAll();
    }

    public List<Subway> findByBtrainNoAndSubwayLine(String btrainNo, String subwayLine){
        return subwayRepository.findByBtrainNoAndSubwayLine(btrainNo,subwayLine);
    }

    public List<Subway> findByUpdatedIsBefore(LocalDateTime time) {
        return subwayRepository.findByUpdatedIsBefore(time);
    }


}
