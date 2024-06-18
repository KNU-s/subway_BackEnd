package com.knu.subway.entity.subwayEnum;

public enum TrainStatus {
    ENTERING(0, "진입"),
    ARRIVED(1, "도착"),
    DEPARTING(2, "출발"),
    DEPARTED_PREVIOUS_STATION(3, "전역 출발"),
    ENTERING_PREVIOUS_STATION(4, "전역 진입"),
    ARRIVED_PREVIOUS_STATION(5, "전역 도착"),
    IN_TRANSIT(99, "운행중");

    private final int code;
    private final String description;

    TrainStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static TrainStatus fromCode(int code) {
        for (TrainStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }

    @Override
    public String toString() {
        return description + " (" + code + ")";
    }
}