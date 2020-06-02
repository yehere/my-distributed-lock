package com.test.spi;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @Author heye
 * @Date 2019/12/31
 * @Decription
 */
public class MainTest {
    public static void main(String[] args) {
        ServiceLoader<MyFactory> load = ServiceLoader.load(MyFactory.class);
        Iterator<MyFactory> it = load.iterator();
        while (it.hasNext()){
            MyFactory factory = it.next();
            try {
                //反射new对象
//                factory.getClass().newInstance().testCreate();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
