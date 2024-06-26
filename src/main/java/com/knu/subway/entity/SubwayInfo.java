package com.knu.subway.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class SubwayInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "SUBWAY_NAME")
    private String subwayName;
    @Column(name = "SUBWAY_LINE")
    private String subwayLine;
    @Column(name = "SUBWAY_LINE_ID")
    private Long subwayLineId;
}
