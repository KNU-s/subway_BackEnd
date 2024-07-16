package com.knu.subway.entity;

import com.knu.subway.entity.subwayEnum.SubwayLine;
import com.knu.subway.entity.subwayEnum.TrainStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@Setter
@Getter
@Document(collection = "subway")
public class Subway {
    @Id
    private String id;
    private String trainId;
    private String statnId;
    private String currentStation;
    //다음역 ID
    private String prevId;
    //이전역 ID
    private String nextId;
    //도착지방면
    private String dstStation;
    //환승 가능 역 ID ( ',' 기준으로 나눠야 함 )
//    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> transferStations;
    //열차 도착 예정 시간(단위: 초)
    private String dstTime;
    //도착 메시지( 3개가 있음 )
    private String dstMessage1;
    private String dstMessage2;
    private String dstMessage3;
    // (0:진입, 1:도착, 2:출발, 3:전역 출발, 4:전역 진입, 5:전역 도착, 99:운행중)
//    @Enumerated(EnumType.STRING)
    private String trainStatus;
    // (0 : 상행/내선, 1 : 하행/외선)
    private String updnLine;
//    @Enumerated(EnumType.STRING)
    private String subwayLine;
    private String direction;
    @Builder
    public Subway(String id,String currentStation, String statnId, String prevId, String nextId, String dstStation, List<String> transferStations, String dstTime, String dstMessage1, String dstMessage2, String dstMessage3, TrainStatus trainStatus, String updnLine, SubwayLine subwayLine,String trainId,String direction) {
        this.id = id;
        this.statnId = statnId;
        this.prevId = prevId;
        this.nextId = nextId;
        this.dstStation = dstStation;
        this.transferStations = transferStations;
        this.dstTime = dstTime;
        this.dstMessage1 = dstMessage1;
        this.dstMessage2 = dstMessage2;
        this.dstMessage3 = dstMessage3;
        this.trainStatus = trainStatus.getDescription();
        this.updnLine = updnLine;
        this.trainId = trainId;
        this.subwayLine = subwayLine.getName();
        this.currentStation = currentStation;
        this.direction = direction;
    }

    public Subway() {

    }
}
