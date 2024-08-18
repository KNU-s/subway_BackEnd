package com.knu.subway.entity;

import com.knu.subway.entity.dto.SubwayDTO;
import com.knu.subway.entity.subwayEnum.TrainStatus;
import java.time.LocalDateTime;
import java.util.Objects;
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
    private LocalDateTime updated;
    private LocalDateTime created;
    @Builder
    public Subway(String id, String statnNm, String statnFNm, String statnTNm, String bstatnNm, String arvlMsg, TrainStatus arvlStatus, String updnLine, String subwayLine, String btrainNo, String direction, String btrainSttus, boolean lstcarAt, LocalDateTime updated,LocalDateTime created) {
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
        this.updated = updated;
        this.created = created;
    }

    public Subway() {

    }

    public SubwayDTO toDTO() {
        SubwayDTO dto = SubwayDTO.builder()
                .btrainNo(this.btrainNo)
                .statnNm(this.statnNm)
                .statnFNm(this.statnFNm)
                .statnTNm(this.statnTNm)
                .bstatnNm(this.bstatnNm)
                .arvlMsg(this.arvlMsg)
                .arvlStatus(this.arvlStatus)
                .updnLine(this.updnLine)
                .subwayLine(this.subwayLine)
                .direction(this.direction)
                .btrainSttus(this.btrainSttus)
                .lstcarAt(this.lstcarAt)
                .updated(this.updated)
                .created(this.created)
                .build();
        return dto;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subway that = (Subway) o;
        return Objects.equals(btrainNo, that.btrainNo);
    }
    @Override
    public int hashCode() {
        return Objects.hash(btrainNo);
    }
}
