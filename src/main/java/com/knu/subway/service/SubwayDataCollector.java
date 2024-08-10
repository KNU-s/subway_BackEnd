package com.knu.subway.service;

import com.knu.subway.entity.StationInfo;
import com.knu.subway.entity.Subway;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 실시간 데이터를 유지하기 위한 DataCollector 클래스입니다.
 * SubwayAsync를 호출하여 사용합니다.
 * MetaData로 넣어둔 StationInfo를 읽어와서, 서울 공공 API를 호출합니다.
 * 또한 중복 데이터 수집을 막기위해 자체적으로 Cookie를 활용하였습니다.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SubwayDataCollector {

    private final SubwayService subwayService;
    private final StationInfoService stationInfoService;
    private List<StationInfo> stationInfoList;
    private Set<String> stationList;
    private final SubwayAsyncService subwayAsyncService;
    private List<String> subwayCookie = new ArrayList<>();

    @PostConstruct
    private void init() {
        this.stationInfoList = stationInfoService.findAll();
        this.stationList = stationInfoList.stream()
                .map(StationInfo::getStationLine)
                .collect(Collectors.toSet());
        subwayService.deleteAll();
        System.out.println("Initialized stationList: " + stationList); // stationList가 예상대로 초기화되었는지 확인
    }
    @Scheduled(fixedRate = 5000)  // 5초마다 데이터 수집
    public void collectData() {
        for(String data : stationList){
            subwayAsyncService.collectDataByLineAsync(data,stationInfoList,subwayCookie);
        }
    }

    @Scheduled(fixedRate = 600000)  // 10분마다 쿠키 초기화
    public void subwayCookie() {
        log.info("delete Subway Cookie {} : ",subwayCookie);
        subwayCookie.clear();
    }

    @Scheduled(fixedRate = 30000)
    public void arrivalSubway() {
        List<Subway> subways = subwayService.findAll();
        for(Subway subway : subways ){
            if(subwayAsyncService.shouldDeleteExistingTrain(subway)){
                subwayService.delete(subway);
                subwayCookie.add(subway.getBtrainNo());
            }
        }
    }
}
