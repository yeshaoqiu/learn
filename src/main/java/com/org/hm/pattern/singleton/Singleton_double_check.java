package com.org.hm.pattern.singleton;

/**
 * 双检懒汉式（可用，推荐）
 */
public class Singleton_double_check {
    private volatile static Singleton_double_check INSTANCE = null;

    private Singleton_double_check(){}

    public static Singleton_double_check getINSTANCE() {
        if(INSTANCE == null){
            synchronized (Singleton_double_check.class){
                if(INSTANCE == null){
                    INSTANCE = new Singleton_double_check();
                }
            }
        }
        return INSTANCE;
    }
}
