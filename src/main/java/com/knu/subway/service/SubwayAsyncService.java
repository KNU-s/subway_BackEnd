package com.knu.subway.service;

import com.knu.subway.entity.StationInfo;
import com.knu.subway.entity.Subway;
import com.knu.subway.entity.dto.SubwayDTO;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import java.util.Set;
import java.util.stream.Collectors;
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
    private final List<String> notSupport = List.of("진접","오남","별내별가람","운천","용문","지평");
    @Async("taskExecutor")
    public void collectDataByLineAsync(String line, List<StationInfo> stationInfoList, List<String> subwayCookie) {
        // Filter out stations from stationInfoList that are in notSupport
        List<StationInfo> filteredStationInfoList = stationInfoList.stream()
                .filter(station -> !notSupport.contains(station.getStationName()))
                .collect(Collectors.toList());

        // Process stations for the given line
        filteredStationInfoList.stream()
                .filter(station -> station.getStationLine().equals(line))
                .forEach(station -> processStation(station, subwayCookie));
    }

    private void processStation(StationInfo station, List<String> subwayCookie) {
        List<SubwayDTO> subwayArrivals = apiService.getSubwayArrivals(station.getStationName());
        saveSubwayInfo(subwayArrivals, subwayCookie);
    }

    private void saveSubwayInfo(List<SubwayDTO> subwayDTOList, List<String> subwayCookie) {
        List<Subway> save = new ArrayList<>();
        subwayDTOList.forEach(subwayDTO -> {
            Subway subway = subwayDTO.toEntity();
            List<Subway> existingSubways = subwayService.findByBtrainNoAndSubwayLine(subwayDTO.getBtrainNo(), subwayDTO.getSubwayLine());

            if (!existingSubways.isEmpty()) {
                Subway existingSubway = existingSubways.get(0);
                if(!subwayEquals(existingSubway, subwayDTO)) {
                    Subway update = subwayService.update(existingSubway, subwayDTO);
                    save.add(update);
                }

            } else if (!subwayCookie.contains(subway.getBtrainNo())) {
//                subwayService.save(subway);
                save.add(subway);
            }
        });
        Set<Subway> saveAll = new HashSet<>();
        for (Subway subway : save) {
            saveAll.add(subway);
        }
        subwayService.saveAll(saveAll.stream().toList());
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
