package com.knu.subway.service;

import com.knu.subway.entity.StationInfo;
import com.knu.subway.entity.Subway;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
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
    private final List<String> notSupport = List.of("진접","오남","별내별가람","운천","용문","지평","임진강");
    private final List<String> confusionStation = List.of("1호선-(인천)","2호선-(천안-신창)","2호선-(내선순환)","3호선","4호선","6호선","9호선","신분당선","7호선");

    // 패턴 교체를 위한 상태 변수
    private boolean processFirstPattern = true;

    @PostConstruct
    private void init() {
        this.stationInfoList = stationInfoService.findAll();
        this.stationList = stationInfoList.stream()
                .map(StationInfo::getStationLine)
                .collect(Collectors.toSet());
        subwayService.deleteAll();
        System.out.println("Initialized stationList: " + stationList); // stationList가 예상대로 초기화되었는지 확인
    }
    // 7:30~9:30, 17:30~19:30 -> 6초마다 호출
    @Scheduled(cron = "*/6 30-59 7-9 * * *", zone = "Asia/Seoul")
    @Scheduled(cron = "*/6 30-59 17-19 * * *", zone = "Asia/Seoul")
    public void collectDataDuringRushHour() {
        LocalTime currentTime = LocalTime.now(ZoneId.of("Asia/Seoul"));
        log.info("지금은 출퇴근 시간입니다 {}",currentTime);
        collectData(2);
    }

    @Scheduled(cron = "*/12 * * * * *")
    public void collectDataOutsideRushHour() {
        // 현재 시간을 가져옵니다.
        LocalTime currentTime = LocalTime.now(ZoneId.of("Asia/Seoul"));

        // 오전 2시부터 오전 5시 사이인 경우 작업을 실행하지 않습니다.
        if (currentTime.isAfter(LocalTime.of(0, 40)) && currentTime.isBefore(LocalTime.of(5, 30))) {
            return;
        }

        // 7:30~9:30, 17:30~19:30 시간대에는 작업을 실행하지 않습니다.
        if ((currentTime.isAfter(LocalTime.of(7, 30)) && currentTime.isBefore(LocalTime.of(9, 30))) ||
                (currentTime.isAfter(LocalTime.of(17, 30)) && currentTime.isBefore(LocalTime.of(19, 30)))) {
            return;
        }

        // confusionStation에 포함된 역이 있는지 확인
        boolean hasConfusionStation = stationInfoList.stream()
                .anyMatch(station -> confusionStation.contains(station.getStationLine()));

        if (hasConfusionStation) {
            collectData(4);
        } else {
            collectData(3);
        }
    }


    private void collectData(int numPattern) {
        // 번갈아 가며 첫 번째 패턴과 두 번째 패턴을 처리합니다.
        for (String data : stationList) {
            subwayAsyncService.collectDataByLineAsync(data, filterStationsByPattern(stationInfoList, processFirstPattern, numPattern), subwayCookie);
        }

        // 패턴을 교체합니다.
        processFirstPattern = !processFirstPattern;
    }
    // 특정 패턴(3의 배수 간격)을 기준으로 역 정보를 필터링하는 메서드
    private List<StationInfo> filterStationsByPattern(List<StationInfo> stationInfoList, boolean isFirstPattern, int numPattern) {
        // 기본 필터링: 패턴에 맞는 역만 포함
        List<StationInfo> filteredStations = stationInfoList.stream()
                .filter(station -> (station.getOrder()) % numPattern == (isFirstPattern ? 0 : 1))
                .filter(station -> !notSupport.contains(station.getStationName()))
                .collect(Collectors.toList());

        // 마지막 역을 포함시키기 위해 필터링된 리스트에 추가
        if (!stationInfoList.isEmpty()) {
            StationInfo lastStation = stationInfoList.get(stationInfoList.size() - 1);

            if (!filteredStations.contains(lastStation) && !notSupport.contains(lastStation.getStationName())) {
                filteredStations.add(lastStation);
            }
        }

        return filteredStations;
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