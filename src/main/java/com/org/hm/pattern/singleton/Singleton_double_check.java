package com.org.hm.pattern.singleton;

/**
 * 双检懒汉式（可用，推荐）
 *
 * 反序列化问题处理
 *  implements java.io.Serializable
 *  private Object readResolve() {
 *      return INSTANCE;
 *  }
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
