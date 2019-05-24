package com.org.hm.pattern.singleton;

/**
 * 静态内部类（可用，推荐）
 *
 * 反序列化问题处理
 *  implements java.io.Serializable
 *  private Object readResolve() {
 *      return Singleton_inner_static_class_holder.INSTANCE;
 *  }
 */
public class Singleton_inner_static_class {
    private Singleton_inner_static_class(){}

    private static class Singleton_inner_static_class_holder{
        private static final Singleton_inner_static_class INSTANCE = new Singleton_inner_static_class();
    }

    public static Singleton_inner_static_class getInstance(){
        return Singleton_inner_static_class_holder.INSTANCE;
    }
}
