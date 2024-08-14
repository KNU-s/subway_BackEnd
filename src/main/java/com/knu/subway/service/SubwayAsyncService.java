package com.knu.subway.service;

import com.knu.subway.entity.StationInfo;
import com.knu.subway.entity.Subway;
import com.knu.subway.entity.dto.SubwayDTO;
import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Subway 비동기를 처리하기 위한 클래스입니다.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SubwayAsyncService {

    private final ApiService apiService;
    private final SubwayService subwayService;

    @Async("taskExecutor")
    public void collectDataByLineAsync(String line, List<StationInfo> stationInfoList, List<String> subwayCookie) {
        stationInfoList.stream()
                .filter(station -> station.getStationLine().equals(line))
                .forEach(station -> processStation(station, subwayCookie));
    }

    private void processStation(StationInfo station, List<String> subwayCookie) {
        List<SubwayDTO> subwayArrivals = apiService.getSubwayArrivals(station.getStationName());
        saveSubwayInfo(subwayArrivals, subwayCookie);
    }

    private void saveSubwayInfo(List<SubwayDTO> subwayDTOList, List<String> subwayCookie) {
        subwayDTOList.forEach(subwayDTO -> {
            Subway subway = subwayDTO.toEntity();
            List<Subway> existingSubways = subwayService.findByBtrainNoAndSubwayLine(subwayDTO.getBtrainNo(), subwayDTO.getSubwayLine());

            if (!existingSubways.isEmpty()) {
                Subway existingSubway = existingSubways.get(0);
//                if (shouldDeleteExistingTrain(existingSubway)) {
//                    subwayService.delete(existingSubway);
//                    subwayCookie.add(existingSubway.getBtrainNo());
//                } else if(!shouldDeleteExistingTrain(existingSubway) && !subwayEquals(existingSubway, subwayDTO)){
                if(!subwayEquals(existingSubway, subwayDTO)) {
                    subwayService.update(existingSubway, subwayDTO);
                }

            } else if (!subwayCookie.contains(subway.getBtrainNo())) {
                subwayService.save(subway);
            }
        });
    }

    public boolean shouldDeleteExistingTrain(Subway subway) {
        return subway.getBstatnNm() != null && subway.getStatnNm() != null && subway.getArvlStatus() != null &&
                Objects.equals(subway.getBstatnNm(), subway.getStatnNm()) && subway.getArvlStatus().equals("도착");
    }

    private boolean subwayEquals(Subway subway, SubwayDTO subwayDTO) {
        return subway.getStatnFNm().equals(subwayDTO.getStatnFNm()) && subway.getStatnNm().equals(subwayDTO.getStatnNm()) &&
                subway.getStatnTNm().equals(subwayDTO.getStatnTNm()) && subway.getArvlMsg().equals(subwayDTO.getArvlMsg()) &&
                subway.getBstatnNm().equals(subwayDTO.getBstatnNm());
    }
}
