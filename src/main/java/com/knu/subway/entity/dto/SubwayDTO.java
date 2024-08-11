package com.knu.subway.entity.dto;

import com.knu.subway.entity.Subway;
import com.knu.subway.entity.subwayEnum.TrainStatus;
import java.time.LocalDateTime;
import lombok.Builder;
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
    private LocalDateTime created;
    private LocalDateTime updated;
    @Builder(toBuilder = true)
    public SubwayDTO(String statnNm, String statnFNm, String statnTNm, String bstatnNm, String arvlMsg, String arvlStatus, String updnLine, String subwayLine, String btrainNo, String direction, String btrainSttus, boolean lstcarAt, LocalDateTime updated,LocalDateTime created) {
        this.statnNm = statnNm;
        this.statnFNm = statnFNm;
        this.statnTNm = statnTNm;
        this.bstatnNm = bstatnNm;
        this.arvlMsg = arvlMsg;
        this.arvlStatus = arvlStatus;
        this.updnLine = updnLine;
        this.btrainNo = btrainNo;
        this.subwayLine = subwayLine;
        this.direction = direction;
        this.btrainSttus = btrainSttus;
        this.lstcarAt = lstcarAt;
        this.created = created;
        this.updated = updated;
    }

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
                .updated(this.updated)
                .build();
    }

    public SubwayDTO(){

    }
}
