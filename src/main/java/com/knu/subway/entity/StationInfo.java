package com.knu.subway.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Setter
@Getter
@Document(collection = "station_info")
public class StationInfo {
    @Id
    private String stationId;
    private String stationName;
    private String stationLine;
    private Long stationLineId;
}
