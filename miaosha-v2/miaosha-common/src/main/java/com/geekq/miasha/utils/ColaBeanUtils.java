package com.geekq.miasha.utils;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;


public class ColaBeanUtils extends BeanUtils {


    public static <S, T> T copyBeanProperties(S source, Supplier<T> target) {
        T t = target.get();
        copyProperties(source, t);
        return t;
    }


    public static <S, T> T copyBeanProperties(S source, Supplier<T> target, ColaBeanUtilsCallBack<S, T> callBack) {
        T t = target.get();
        copyProperties(source, t);
        if (callBack != null) {
            // 回调
            callBack.callBack(source, t);
        }
        return t;
    }


    public static <S, T> List<T> copyListProperties(List<S> sources, Supplier<T> target) {
        return copyListProperties(sources, target, null);
    }


    public static <S, T> List<T> copyListProperties(List<S> sources, Supplier<T> target, ColaBeanUtilsCallBack<S, T> callBack) {
        List<T> list = new ArrayList<>(sources.size());
        for (S source : sources) {
            T t = target.get();
            copyProperties(source, t);
            list.add(t);
            if (callBack != null) {
                // 回调
                callBack.callBack(source, t);
            }
        }
        return list;
    }
}
