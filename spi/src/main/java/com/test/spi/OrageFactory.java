package com.test.spi;

/**
 * @Author heye
 * @Date 2019/12/31
 * @Decription
 */
public class OrageFactory implements MyFactory {
    static {
        System.out.println("我是OrageFactory静态代码块");
    }
    @Override
    public void testCreate() {
        System.out.println("我是OrageFactory");
    }
}
