package com.knu.subway.service;

import com.knu.subway.entity.StationInfo;
import com.knu.subway.entity.Subway;
import com.knu.subway.entity.dto.SubwayDTO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
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

    private void saveSubwayInfo(List<SubwayDTO> subwayDTO){
        subways = new ArrayList<>();
        for(SubwayDTO data : subwayDTO){
            Subway subway = data.toEntity();
//            if(subway.getDstStation())
            List<Subway> trainId = subwayService.findByTrainId(subway.getTrainId());
            if(trainId.isEmpty()) {
                subways.add(subway);
            }
        }
        subwayService.saveAll(subways);
    }
}
