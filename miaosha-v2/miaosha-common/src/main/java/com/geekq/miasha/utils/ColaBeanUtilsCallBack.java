package com.geekq.miasha.utils;


@FunctionalInterface
public interface ColaBeanUtilsCallBack<S, T> {
    void callBack(S t, T s);
}
