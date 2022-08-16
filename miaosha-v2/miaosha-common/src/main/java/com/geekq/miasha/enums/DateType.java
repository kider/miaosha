package com.geekq.miasha.enums;


public enum DateType {
    NORM_DATE_PATTERN("yyyy-MM-dd"),
    NORM_DATETIME_PATTERN("yyyy-MM-dd HH:mm:ss"),
    NORM_TIME_PATTERN("HH:mm:ss"),
    NORM_DATETIME_MINUTE_PATTERN("yyyy-MM-dd HH:mm"),
    NORM_DATETIME_MS_PATTERN("yyyy-MM-dd HH:mm:ss.SSS"),
    CHINESE_DATE_PATTERN("yyyy年MM月dd日"),
    PURE_DATE_PATTERN("yyyyMMdd"),
    PURE_TIME_PATTERN("HHmmss"),
    PURE_DATETIME_PATTERN("yyyyMMddHHmmss"),
    PURE_DATETIME_MS_PATTERN("yyyyMMddHHmmssSSS"),
    PURE_DATE_MD_PATTERN("MMdd"),
    PURE_DATE_YM_PATTERN("yyyyMM"),
    PURE_DATE_DD_PATTERN("dd");

    private String format;

    DateType(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }
}
