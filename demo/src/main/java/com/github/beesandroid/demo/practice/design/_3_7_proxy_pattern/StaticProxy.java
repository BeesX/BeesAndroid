package com.github.beesandroid.demo.practice.design._3_7_proxy_pattern;

// 静态代理类，与被代理类实现同一套接口
public class StaticProxy implements Subject {

    private Subject mSubject;

    public StaticProxy(Subject subject) {
        mSubject = subject;
    }

    @Override
    public void visit() {
        mSubject.visit();
    }
}
