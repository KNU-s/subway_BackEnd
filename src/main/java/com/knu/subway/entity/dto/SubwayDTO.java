package com.knu.subway.entity.dto;

import com.knu.subway.entity.Subway;
import com.knu.subway.entity.subwayEnum.TrainStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubwayDTO {
    private String btrainNo;
    private String statnNm;
    private String statnFNm;
    private String statnTNm;
    private String bstatnNm;
    private String arvlMsg;
    private String arvlStatus;
    private String updnLine;
    private String subwayLine;
    private String direction;
    private String btrainSttus;
    private boolean lstcarAt;

    @Override
    public String toString() {
        return "SubwayDTO{" +
                ", trainId='" + btrainNo + '\'' +
                ", statnNm='" + statnNm + '\'' +
                ", statnFNm='" + statnFNm + '\'' +
                ", statnTNm='" + statnTNm + '\'' +
                ", trainLineNm='" + bstatnNm + '\'' +
                ", arvlMsg='" + arvlMsg + '\'' +
                ", arvlStatus='" + arvlStatus + '\'' +
                ", updnLine='" + updnLine + '\'' +
                ", subwayLine='" + subwayLine + '\'' +
                ", direction='" + direction + '\'' +
                ", btrainSttus='" + btrainSttus + '\'' +
                ", btrainSttus='" + lstcarAt + '\'' +
                '}';
    }
    public Subway toEntity() {
        return Subway.builder()
                .btrainNo(this.btrainNo)
                .statnNm(this.statnNm)
                .statnFNm(this.statnFNm)
                .statnTNm(this.statnTNm)
                .bstatnNm(this.bstatnNm)
                .arvlMsg(this.arvlMsg)
                .arvlStatus(TrainStatus.fromCode(this.arvlStatus))
                .updnLine(this.updnLine)
                .subwayLine(this.subwayLine)
                .direction(this.direction)
                .btrainSttus(this.btrainSttus)
                .lstcarAt(this.lstcarAt)
                .build();
    }
}
