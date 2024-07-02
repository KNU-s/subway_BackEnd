package com.knu.subway.entity.subwayEnum;

public enum TrainStatus {
    진입("0", "진입"),
    도착("1", "도착"),
    출발("2", "출발"),
    전역_출발("3", "전역 출발"),
    전역_진입("4", "전역 진입"),
    전역_도착("5", "전역 도착"),
    운행중("99", "운행중");

    private final String code;
    private final String description;

    TrainStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static TrainStatus fromCode(String code) {
        for (TrainStatus status : values()) {
            if (status.code.equals(code)) {
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