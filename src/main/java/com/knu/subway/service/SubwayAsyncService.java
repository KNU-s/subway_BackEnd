package com.knu.subway.service;

import com.knu.subway.entity.StationInfo;
import com.knu.subway.entity.Subway;
import com.knu.subway.entity.dto.SubwayDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Subway 비동기를 처리하기 위한 클래스입니다.
 *
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
                .forEach(station -> {
                    List<SubwayDTO> subwayArrivals = apiService.getSubwayArrivals(station.getStationName());
                    saveSubwayInfo(subwayArrivals, subwayCookie);
                });
    }

    private void saveSubwayInfo(List<SubwayDTO> subwayDTOList, List<String> subwayCookie) {
        List<Subway> subwaysToSave = new ArrayList<>();
        List<Subway> subwaysToDelete = new ArrayList<>();
        for (SubwayDTO subwayDTO : subwayDTOList) {
            Subway subway = subwayDTO.toEntity();
            processSubway(subway, subwayCookie, subwaysToSave, subwaysToDelete);
        }
        subwayService.deleteAll(subwaysToDelete);
        subwayService.saveAll(subwaysToSave);
    }

    private void processSubway(Subway subway, List<String> subwayCookie, List<Subway> subwaysToSave, List<Subway> subwaysToDelete) {
        List<Subway> existingTrains = subwayService.findByBtrainNo(subway.getBtrainNo());
        if (existingTrains.isEmpty()) {
            handleNewTrain(subway, subwayCookie, subwaysToSave);
        } else {
            handleExistingTrain(subway, existingTrains, subwayCookie, subwaysToSave, subwaysToDelete);
        }
    }

    private void handleNewTrain(Subway subway, List<String> subwayCookie, List<Subway> subwaysToSave) {
        if (!subwayCookie.contains(subway.getBtrainNo())) {
            subwaysToSave.add(subway);
        }
    }

    private void handleExistingTrain(Subway subway, List<Subway> existingTrains, List<String> subwayCookie, List<Subway> subwaysToSave, List<Subway> subwaysToDelete) {
        Subway existingTrain = existingTrains.get(0);
        if (shouldDeleteExistingTrain(subway, existingTrain)) {
            subwaysToDelete.add(existingTrain);
            subwayCookie.add(existingTrain.getBtrainNo());
        } else {
            updateOrAddSubway(subway, existingTrain, subwaysToSave);
        }
    }

    private void updateOrAddSubway(Subway subway, Subway existingTrain, List<Subway> subwaysToSave) {
        Subway updatedSubway = subwayService.update(existingTrain, subway.toDTO());
        if (subwaysToSave.stream().noneMatch(s -> Objects.equals(s.getBtrainNo(), updatedSubway.getBtrainNo()))) {
            subwaysToSave.add(updatedSubway);
        }
    }

    private boolean shouldDeleteExistingTrain(Subway subway, Subway existingTrain) {
        return subway.getBstatnNm() != null && subway.getStatnNm() != null && subway.getArvlStatus() != null &&
                subway.getBstatnNm().contains(subway.getStatnNm()) && "도착".equals(subway.getArvlStatus());
    }
}
