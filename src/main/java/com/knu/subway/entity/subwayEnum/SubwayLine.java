package com.knu.subway.entity.subwayEnum;
public enum SubwayLine {
    _1호선("1001", "1호선"),
    _2호선("1002", "2호선"),
    _3호선("1003", "3호선"),
    _4호선("1004", "4호선"),
    _5호선("1005", "5호선"),
    _6호선("1006", "6호선"),
    _7호선("1007", "7호선"),
    _8호선("1008", "8호선"),
    _9호선("1009", "9호선"),
    중앙선호선("1061", "중앙선"),
    경의중앙선("1063", "경의중앙선"),
    공항철도("1065", "공항철도"),
    경춘선("1067", "경춘선"),
    수의분당선("1075", "수의분당선"),
    신분당선("1077", "신분당선"),
    우이신설선("1092", "우이신설선"),
    서해선("1093", "서해선"),
    경강선("1081", "경강선"),
    GTX_A("1032", "GTX-A");

    private final String code;
    private final String name;

    SubwayLine(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static SubwayLine fromCode(String code) {
        for (SubwayLine line : values()) {
            if (line.code.equals(code)) {
                return line;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }

    @Override
    public String toString() {
        return name + " (" + code + ")";
    }
}
