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
                    List<SubwayDTO> subway = apiService.getSubwayArrivals(station.getStationName());
                    saveSubwayInfo(subway, subwayCookie);
                });
    }

    private void saveSubwayInfo(List<SubwayDTO> subwayDTOList, List<String> subwayCookie) {
        List<Subway> subwaysToSave = new ArrayList<>();
        List<Subway> subwaysToDelete = new ArrayList<>();
        for (SubwayDTO data : subwayDTOList) {
            Subway subway = data.toEntity();
            String dstStation = subway.getBstatnNm();
            String curStation = subway.getStatnNm();
            String trainStatus = subway.getArvlStatus();
            List<Subway> existingTrains = subwayService.findByBtrainNo(subway.getBtrainNo());
            if (existingTrains.isEmpty()) {
                if (!subwayCookie.contains(subway.getBtrainNo())) {
                    subwaysToSave.add(subway);
                }
            } else {
                if (shouldDeleteExistingTrain(dstStation, curStation, trainStatus)) {
                    Subway existingTrain = existingTrains.get(0);
                    subwaysToDelete.add(existingTrain);
                    subwayCookie.add(existingTrain.getBtrainNo());
                } else {
                    Subway updatedSubway = subwayService.update(existingTrains.get(0), data);
                    for(Subway subwayData : subwaysToSave){
                        if(Objects.equals(subwayData.getBtrainNo(),updatedSubway.getBtrainNo())){
                            break;
                        }
                    }
                    subwaysToSave.add(updatedSubway);
                }
            }
        }
        subwayService.deleteAll(subwaysToDelete);
        subwayService.saveAll(subwaysToSave);
    }

    private boolean shouldDeleteExistingTrain(String dstStation, String curStation, String trainStatus) {
        return dstStation != null && curStation != null && trainStatus != null &&
                dstStation.contains(curStation) && "도착".equals(trainStatus);
    }
}