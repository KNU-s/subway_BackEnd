package com.knu.subway.entity;

import jakarta.persistence.*;

@Entity
public class SubwayInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "SUBWAY_NAME")
    private String subwayName;
    @Column(name = "SUBWAY_LINE")
    private String subwayLine;
    @Column(name = "SUBWAY_LINE_ID")
    private Long subwayLineId;
}
