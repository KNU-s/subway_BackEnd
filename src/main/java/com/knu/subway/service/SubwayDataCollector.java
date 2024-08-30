package com.knu.subway.service;

import com.knu.subway.entity.StationInfo;
import com.knu.subway.entity.Subway;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    @Scheduled(cron = "0 */5 16-17 * * *")
    public void collectData() {
        for (String data : stationList) {
            subwayAsyncService.collectDataByLineAsync(data, stationInfoList, subwayCookie);
        }
    }



    @Scheduled(cron = "0 */10 * * * *")
    public void subwayCookie() {
        log.info("delete Subway Cookie {} : ",subwayCookie);
        subwayCookie.clear();
    }

    @Scheduled(cron = "0 0/3 * * * ?")
    public void processOldSubways() {
        // 현재 시간에서 5분 전 계산
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().plusHours(9).minusMinutes(5);

        // 5분 전보다 업데이트된 데이터 조회
        List<Subway> subways = subwayService.findByUpdatedIsBefore(fiveMinutesAgo);
        if(!subways.isEmpty()) {
            for (Subway subway : subways) {
                // 데이터가 5분 이상 된 경우 처리
                subwayService.delete(subway);
                subwayCookie.add(subway.getBtrainNo());
            }
        }
    }
    @Scheduled(cron = "0 0 5 * * *")
    public void resetSubway(){
        subwayService.deleteAll();
    }
}
