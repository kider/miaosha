package com.geekq.miasha.enums;


public enum YesOrNo {

    YES(1),            // 是	有错误
    NO(0);            // 否	无错误

    public final int value;

    YesOrNo(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
