package com.knu.subway.service;

import com.knu.subway.entity.StationInfo;
import com.knu.subway.entity.Subway;
import com.knu.subway.entity.dto.SubwayDTO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Slf4j
@RequiredArgsConstructor
@Service
public class SubwayDataCollector {

    private final SubwayService subwayService;
    private final StationInfoService stationInfoService;
    private final ApiService apiService;
    private List<StationInfo> stationInfoList;
    private Set<String> stationList;
    private List<String> subwayCookie = new ArrayList<>();

    @PostConstruct
    private void init() {
        this.stationInfoList = stationInfoService.findAll();
        this.stationList = stationInfoList.stream()
                .map(StationInfo::getStationLine)
                .collect(Collectors.toSet());

        System.out.println("Initialized stationList: " + stationList); // stationList가 예상대로 초기화되었는지 확인
    }
    @Scheduled(fixedRate = 10000)  // 10초마다 데이터 수집
    public void collectData() {
        stationList.forEach(this::collectDataByLineAsync); // 비동기 메서드 호출
    }

    @Scheduled(fixedRate = 1800000)  // 30분마다 쿠키 초기화
    public void subwayCookie() {
        log.info("delete Subway Cookie {} : ",subwayCookie);
        subwayCookie.clear();
    }

    @Async("taskExecutor")
    public void collectDataByLineAsync(String line) {
        stationInfoList.stream()
                .filter(station -> station.getStationLine().equals(line))
                .forEach(station -> {
                    List<SubwayDTO> subway = apiService.getSubwayArrivals(station.getStationName());
                    saveSubwayInfo(subway);
                });
    }

    @Scheduled(fixedRate = 10800000) // 3 hours in milliseconds
    public void deleteAllSubways() {
        subwayService.deleteAll();
        System.out.println("Deleted all subway entries");
    }
    private void saveSubwayInfo(List<SubwayDTO> subwayDTOList) {
        List<Subway> subwaysToSave = new ArrayList<>();
        for (SubwayDTO data : subwayDTOList) {
            Subway subway = data.toEntity();
            String dstStation = subway.getDstStation();
            String curStation = subway.getStatnId();
            String trainStatus = subway.getTrainStatus();
            List<Subway> existingTrains = subwayService.findByTrainId(subway.getTrainId());
            //열차번호가 존재하지 않는다면
            if (existingTrains.isEmpty()) {
                //최근에 삭제된 열차가 아니라면
                if (!subwayCookie.contains(subway.getTrainId())) {
                    subwaysToSave.add(subway);
                }
            //열자번호가 존재한다면
            } else {
                //현재 열차가 도착 상태이고, 마지막 역 까지 도달했다면
                if (shouldDeleteExistingTrain(dstStation, curStation, trainStatus)) {
                    Subway existingTrain = existingTrains.get(0);
                    subwayService.delete(existingTrain);
                    subwayCookie.add(existingTrain.getTrainId());
                } else {
                    Subway updatedSubway = subwayService.update(existingTrains.get(0), data);
                    subwaysToSave.add(updatedSubway);
                }
            }
        }

        subwayService.saveAll(subwaysToSave);
    }

    private boolean shouldDeleteExistingTrain(String dstStation, String curStation, String trainStatus) {
        return dstStation != null && curStation != null && trainStatus != null &&
                dstStation.contains(curStation) && "도착".equals(trainStatus);
    }

}
