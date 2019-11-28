package com.org.hm.pattern.singleton;

import java.sql.SQLOutput;

/**
 * 枚举（可用，强烈推荐）
 * 解决反序列化破坏单例
 *
 */
public enum Singleton_enum {
    INSTANCE;

    private Object data;

    Singleton_enum(){
        System.out.println("init once");
        data = new String("private data");
    }

    //usage
    public Object whateverMethod(){
        return data;
    }

    //usage
    public static void main(String[] args) {
        System.out.println(Singleton_enum.INSTANCE.whateverMethod());

        System.out.println(Singleton_enum.INSTANCE.whateverMethod());

        System.out.println(Singleton_enum.INSTANCE.whateverMethod());

        System.out.println(Singleton_enum.INSTANCE.whateverMethod().equals(Singleton_enum.INSTANCE.whateverMethod()));
    }
}
