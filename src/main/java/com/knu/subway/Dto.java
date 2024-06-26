package com.knu.subway;

import com.knu.subway.entity.Subway;
import com.knu.subway.entity.subwayEnum.SubwayLine;
import com.knu.subway.entity.subwayEnum.TrainStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Dto {
    private String statnId;
    private Long prevId;
    //이전역 ID
    private Long nextId;
    //도착지방면
    private String dstStation;
    //환승 가능 역 ID ( ',' 기준으로 나눠야 함 )
    private List<String> transferStations;
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
//    @JsonProperty("trainLineNm")
//    private String trainLineNm;
//
//    @JsonProperty("statnId")
//    private String statnId;
//
//    @JsonProperty("statnNm")
//    private String statnNm;
//
//    @JsonProperty("barvlDt")
//    private String barvlDt;
//
//    @JsonProperty("btrainNo")
//    private String btrainNo;
//
//    @JsonProperty("bstatnId")
//    private String bstatnId;
//
//    @JsonProperty("bstatnNm")
//    private String bstatnNm;
//
//    @JsonProperty("recptnDt")
//    private String recptnDt;
//
//    @JsonProperty("arvlMsg2")
//    private String arvlMsg2;
//
//    @JsonProperty("arvlMsg3")
//    private String arvlMsg3;
//
//    @JsonProperty("arvlCd")
//    private String arvlCd;


    public Subway toEntity(){
        Subway subway = Subway.builder()
                .statnId(statnId)
                .prevId(prevId)
                .nextId(nextId)
                .dstStation(dstStation)
                .transferStations(transferStations)
                .dstTime(dstTime)
                .dstMessage1(dstMessage1)
                .dstMessage2(dstMessage2)
                .trainStatus(trainStatus)
                .updnLine(updnLine)
                .subwayLine(subwayLine)
                .build();
        return subway;
    }
}
