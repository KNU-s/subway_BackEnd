package com.knu.subway.entity.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SubwayDTO {
    private String statnId;
    private String prevId;
    private String nextId;
    private String dstStation;
    private List<String> transferStations;
    private String dstTime;
    private String dstMessage1;
    private String dstMessage2;
    private String dstMessage3;
    private String trainStatus;
    private String updnLine;
    private String subwayLine;

    @Override
    public String toString() {
        return "SubwayDTO{" +
                "statnId='" + statnId + '\'' +
                ", prevId='" + prevId + '\'' +
                ", nextId='" + nextId + '\'' +
                ", dstStation='" + dstStation + '\'' +
                ", transferStations=" + transferStations +
                ", dstTime='" + dstTime + '\'' +
                ", dstMessage1='" + dstMessage1 + '\'' +
                ", dstMessage2='" + dstMessage2 + '\'' +
                ", dstMessage3='" + dstMessage3 + '\'' +
                ", trainStatus='" + trainStatus + '\'' +
                ", updnLine='" + updnLine + '\'' +
                ", subwayLine='" + subwayLine + '\'' +
                '}';
    }
}
