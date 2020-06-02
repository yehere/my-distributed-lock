package com.test.lock.util;

import java.lang.reflect.Field;

/**
 * @Author heye
 * @Date 2020/1/15
 * @Decription 转换工具
 */
public class TransferUtils {
    private TransferUtils() {
    }

    /**
     * 数据填充 从from填充到to
     * @param from
     * @param to
     */
    public static void fillFromTO(Object from, Object to) {
        Class fc = from.getClass();
        Class tc = to.getClass();

        Field[] toFileds = tc.getDeclaredFields();
        Field[] fcFileds = fc.getDeclaredFields();

        for (Field toFiled : toFileds) {
            try {
                toFiled.setAccessible(true);
                //注意：这里未判断是否为基础类型
                if (toFiled.get(to) == null) {
                    for (Field fcFiled : fcFileds) {
                        fcFiled.setAccessible(true);
                        if (fcFiled.getName().equals(toFiled.getName()) && fcFiled.getType() == toFiled.getType()) {
                            toFiled.set(to, fcFiled.get(from));
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
