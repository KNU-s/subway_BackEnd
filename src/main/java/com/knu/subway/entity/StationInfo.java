package com.knu.subway.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Setter
@Getter
@Document(collection = "station_info")
public class StationInfo {
    @Id
    private String id;
    private String stationId;
    private String stationName;
    private String stationLine;
    private Long stationLineId;
    private int order;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StationInfo that = (StationInfo) o;
        return Objects.equals(stationName, that.stationName);
    }
    @Override
    public int hashCode() {
        return Objects.hash(stationName);
    }
}
