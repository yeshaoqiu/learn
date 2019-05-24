package com.org.hm.pattern.singleton;

/**
 * 懒汉式（可用）
 */
public class Singleton_lazy_safe {
    private static Singleton_lazy_safe INSTANCE = null;

    private Singleton_lazy_safe(){}

    public static synchronized Singleton_lazy_safe getINSTANCE() {
        if(INSTANCE == null){
            INSTANCE = new Singleton_lazy_safe();
        }
        return INSTANCE;
    }
}
