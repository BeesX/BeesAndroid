package com.github.beesandroid.demo.practice.design._3_7_proxy_pattern;

// 被代理类，完成实际的功能。
public class ConcreteSubject implements Subject {

    @Override
    public void visit() {
        System.out.println("visit");
    }
}
