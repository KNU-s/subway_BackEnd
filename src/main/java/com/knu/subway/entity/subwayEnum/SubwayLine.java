package com.knu.subway.entity.subwayEnum;
public enum SubwayLine {
    LINE_1(1001, "1호선"),
    LINE_2(1002, "2호선"),
    LINE_3(1003, "3호선"),
    LINE_4(1004, "4호선"),
    LINE_5(1005, "5호선"),
    LINE_6(1006, "6호선"),
    LINE_7(1007, "7호선"),
    LINE_8(1008, "8호선"),
    LINE_9(1009, "9호선"),
    JUNGANG(1061, "중앙선"),
    GYEONGUI_JUNGANG(1063, "경의중앙선"),
    AIRPORT(1065, "공항철도"),
    GYEONGCHUN(1067, "경춘선"),
    SUIN_BUNDANG(1075, "수의분당선"),
    SINBUNDANG(1077, "신분당선"),
    UI_SINSEOL(1092, "우이신설선"),
    SEOHAE(1093, "서해선"),
    GYEONGGANG(1081, "경강선"),
    GTX_A(1032, "GTX-A");

    private final int code;
    private final String name;

    SubwayLine(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static SubwayLine fromCode(int code) {
        for (SubwayLine line : values()) {
            if (line.code == code) {
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
