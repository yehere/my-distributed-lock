package com.example.redissentinel.test;

/**
 * @Author heye
 * @Date 2020/1/7
 * @Decription
 */
public class Parentcs {
    public void sys1(){
        this.sys2();
    }

    public void sys2(){
        System.out.println("父类sys2");
    }
}
