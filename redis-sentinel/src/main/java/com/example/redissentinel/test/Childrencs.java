package com.example.redissentinel.test;

/**
 * @Author heye
 * @Date 2020/1/7
 * @Decription
 */
public class Childrencs extends Parentcs {
    @Override
    public void sys2() {
        System.out.println("子类sys2");
    }

    public static void main(String[] args) {
        Childrencs child = new Childrencs();
        child.sys1();
    }
}
