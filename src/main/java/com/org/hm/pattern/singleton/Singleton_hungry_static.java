package com.org.hm.pattern.singleton;

/**
 * 饿汉式（可用）
 */
public class Singleton_hungry_static {
    private static Singleton_hungry_static INSTANCE = null;

    static {
        INSTANCE = new Singleton_hungry_static();
    }

    public static Singleton_hungry_static getINSTANCE() {
        return INSTANCE;
    }
}
