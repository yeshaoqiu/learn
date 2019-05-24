package com.org.hm.pattern.singleton;

/**
 * 懒汉式(不可用)
 */
public class Singleton_lazy_unsafe {
    private static Singleton_lazy_unsafe INSTANCE = null;

    private Singleton_lazy_unsafe(){}

    public static Singleton_lazy_unsafe getInstance(){
        if(INSTANCE == null){
            INSTANCE = new Singleton_lazy_unsafe();
        }
        return INSTANCE;
    }
}
