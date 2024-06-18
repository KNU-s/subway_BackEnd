package com.knu.subway.entity;

import com.knu.subway.entity.subwayEnum.SubwayLine;
import com.knu.subway.entity.subwayEnum.TrainStatus;
import jakarta.persistence.*;

@Entity
public class Subway {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    //다음역 ID
    private Long prevId;
    //이전역 ID
    private Long nextId;
    //도착지방면
    private String dstStation;
    //환승 가능 역 ID ( ',' 기준으로 나눠야 함 )
    private String transferStations;
    //열차 도착 예정 시간(단위: 초)
    private Long dstTime;
    //도착 메시지( 3개가 있음 )
    private String dstMessage1;
    private String dstMessage2;
    private String dstMessage3;
    // (0:진입, 1:도착, 2:출발, 3:전역 출발, 4:전역 진입, 5:전역 도착, 99:운행중)
    private TrainStatus trainStatus;
    // (0 : 상행/내선, 1 : 하행/외선)
    private String updnLine;
    private SubwayLine subwayLine;

}
