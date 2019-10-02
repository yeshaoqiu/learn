package com.org.hm.thread;

public class Volatile extends Base {
    public static void main(String[] args) {
        ARunnable a =  new ARunnable();
        new Thread(a).start();
        a.tellToStop();
    }

    public static class ARunnable implements Runnable{
        //信号量 线程在预设的地点检测flag，来决定是否停止
        private volatile boolean stop;

        public void tellToStop(){
            stop = true;
        }

        @Override
        public void run() {
            println("进入不可停止区域 1。。。");
            doingLongTime(5);
            println("退出不可停止区域 1。。。");
            println("检测标志stop = %s", String.valueOf(stop));
            if (stop) {
                println("停止执行");
                return;
            }
            println("进入不可停止区域 2。。。");
            doingLongTime(5);
            println("退出不可停止区域 2。。。");
        }
    }
}
