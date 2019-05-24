package com.org.hm.pattern.singleton;

/**
 * 饿汉式（可用）
 */
public class Singleton_hungry {
    private static final Singleton_hungry INSTANCE = new Singleton_hungry();

    private Singleton_hungry(){}

    private static Singleton_hungry getInstance(){
        return INSTANCE;
    }
}
