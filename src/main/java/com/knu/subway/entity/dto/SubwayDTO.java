package com.knu.subway.entity.dto;

import com.knu.subway.entity.Subway;
import com.knu.subway.entity.subwayEnum.TrainStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SubwayDTO {
    private String currentStation;
    private String statnId;
    private String prevStationName;
    private String nextStationName;
    private String dstStation;
    private String trainId;
    private List<String> transferStations;
    private String dstTime;
    private String dstMessage1;
    private String dstMessage2;
    private String dstMessage3;
    private String trainStatus;
    private String updnLine;
    private String subwayLine;
    private String direction;
    private String trainType;
    @Override
    public String toString() {
        return "SubwayDTO{" +
                "currentStation='" + currentStation + '\'' +
                ", statnId='" + statnId + '\'' +
                ", prevId='" + prevStationName + '\'' +
                ", nextId='" + nextStationName + '\'' +
                ", dstStation='" + dstStation + '\'' +
                ", trainId='" + trainId + '\'' +
                ", transferStations=" + transferStations +
                ", dstTime='" + dstTime + '\'' +
                ", dstMessage1='" + dstMessage1 + '\'' +
                ", dstMessage2='" + dstMessage2 + '\'' +
                ", dstMessage3='" + dstMessage3 + '\'' +
                ", trainStatus='" + trainStatus + '\'' +
                ", updnLine='" + updnLine + '\'' +
                ", subwayLine='" + subwayLine + '\'' +
                ", direction='" + direction + '\'' +
                ", trainType='" + trainType + '\'' +
                '}';
    }
    public Subway toEntity() {
        return Subway.builder()
                .statnId(this.statnId)
                .prevStationName(this.prevStationName)
                .nextStationName(this.nextStationName)
                .dstStation(this.dstStation)
                .trainId(this.trainId)
                .transferStations(this.transferStations)
                .dstTime(this.dstTime)
                .dstMessage1(this.dstMessage1)
                .dstMessage2(this.dstMessage2)
                .dstMessage3(this.dstMessage3)
                .trainStatus(TrainStatus.fromCode(this.trainStatus)) // Assuming TrainStatus is an enum
                .updnLine(this.updnLine)
                .subwayLine(this.subwayLine) // Assuming SubwayLine is an enum
                .currentStation(this.currentStation)
                .direction(this.direction)
                .trainType(this.trainType)
                .build();
    }
}
