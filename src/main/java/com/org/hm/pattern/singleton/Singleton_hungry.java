package com.org.hm.pattern.singleton;

/**
 * 饿汉式（可用）
 *
 * 反序列化问题处理
 *  implements java.io.Serializable
 *  private Object readResolve() {
 *      return INSTANCE;
 *  }
 */
public class Singleton_hungry {
    private static final Singleton_hungry INSTANCE = new Singleton_hungry();

    private Singleton_hungry(){}

    private static Singleton_hungry getInstance(){
        return INSTANCE;
    }
}
