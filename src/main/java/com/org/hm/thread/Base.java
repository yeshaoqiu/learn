package com.org.hm.thread;

import java.util.Random;

public abstract class Base {

    protected static void println(String msg, Object... args){
        System.out.println(String.format(msg, args));
    }

    protected static void doingLongTime(long time){
        try{
            Thread.sleep(time*1000);
        }catch (Exception e){
            //
        }
    }

    protected static int random(int bound){
        return new Random().nextInt(bound);
    }
}
