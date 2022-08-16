package com.geekq.miasha.enums;


public enum SexEnum {

    GIRL(0),        // 女
    BOY(1),            // 男
    SECRET(2);        // 保密

    public final int value;

    SexEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
