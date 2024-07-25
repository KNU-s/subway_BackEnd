package com.knu.subway.entity;

import com.knu.subway.entity.subwayEnum.TrainStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@Document(collection = "subway")
public class Subway {
    @Id
    private String id;
    private String btrainNo;
    private String statnNm;
    //이전역
    private String statnFNm;
    //다음역
    private String statnTNm;
    private String bstatnNm;
    private String arvlMsg;
    // (0:진입, 1:도착, 2:출발, 3:전역 출발, 4:전역 진입, 5:전역 도착, 99:운행중)
    private String arvlStatus;
    // (0 : 상행/내선, 1 : 하행/외선)
    private String updnLine;
    private String subwayLine;
    private String direction;
    private String btrainSttus;
    private boolean lstcarAt;
    @Builder
    public Subway(String id, String statnNm, String statnFNm, String statnTNm, String bstatnNm, String arvlMsg, TrainStatus arvlStatus, String updnLine, String subwayLine, String btrainNo, String direction, String btrainSttus, boolean lstcarAt) {
        this.id = id;
        this.statnNm = statnNm;
        this.statnFNm = statnFNm;
        this.statnTNm = statnTNm;
        this.bstatnNm = bstatnNm;
        this.arvlMsg = arvlMsg;
        this.arvlStatus = arvlStatus.getDescription();
        this.updnLine = updnLine;
        this.btrainNo = btrainNo;
        this.subwayLine = subwayLine;
        this.direction = direction;
        this.btrainSttus = btrainSttus;
        this.lstcarAt = lstcarAt;
    }

    public Subway() {

    }
}
