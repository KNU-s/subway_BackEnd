package com.knu.subway.service;

import com.knu.subway.entity.StationInfo;
import com.knu.subway.entity.Subway;
import com.knu.subway.entity.dto.SubwayDTO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SubwayDataCollector {

    private final SubwayService subwayService;
    private final StationInfoService stationInfoService;
    private final ApiService apiService;
    private List<StationInfo> stationInfoList;
    private Set<String> stationList;
    private List<Subway> subways;
    private List<String> subwayCookie;
    private static final Logger log = LoggerFactory.getLogger(ApiService.class);


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
        subwayCookie = new ArrayList<>();
    }

    @Async("taskExecutor")
    public void collectDataByLineAsync(String line) {
//        System.out.println("Collecting data asynchronously for line: " + line); // line 값이 예상대로 출력되는지 확인
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

            if (existingTrains.isEmpty()) {
                if (!subwayCookie.contains(subway.getTrainId())) {
                    subwaysToSave.add(subway);
                }
            } else {
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
