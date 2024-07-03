package com.knu.subway.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Setter
@Getter
@Document(collection = "subway_info")
public class SubwayInfo {
    @Id
    private String id;
    private String subwayName;
    private String subwayLine;
    private Long subwayLineId;
}
