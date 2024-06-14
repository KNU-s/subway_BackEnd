package com.knu.subway;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dto {
    @JsonProperty("trainLineNm")
    private String trainLineNm;

    @JsonProperty("statnId")
    private String statnId;

    @JsonProperty("statnNm")
    private String statnNm;

    @JsonProperty("barvlDt")
    private String barvlDt;

    @JsonProperty("btrainNo")
    private String btrainNo;

    @JsonProperty("bstatnId")
    private String bstatnId;

    @JsonProperty("bstatnNm")
    private String bstatnNm;

    @JsonProperty("recptnDt")
    private String recptnDt;

    @JsonProperty("arvlMsg2")
    private String arvlMsg2;

    @JsonProperty("arvlMsg3")
    private String arvlMsg3;

    @JsonProperty("arvlCd")
    private String arvlCd;

    @Override
    public String toString() {
        return "Dto{" +
                "trainLineNm='" + trainLineNm + '\'' +
                ", statnId='" + statnId + '\'' +
                ", statnNm='" + statnNm + '\'' +
                ", barvlDt='" + barvlDt + '\'' +
                ", btrainNo='" + btrainNo + '\'' +
                ", bstatnId='" + bstatnId + '\'' +
                ", bstatnNm='" + bstatnNm + '\'' +
                ", recptnDt='" + recptnDt + '\'' +
                ", arvlMsg2='" + arvlMsg2 + '\'' +
                ", arvlMsg3='" + arvlMsg3 + '\'' +
                ", arvlCd='" + arvlCd + '\'' +
                '}';
    }
}
