package com.org.hm.thread;

public class Interrupt extends Base {
    public static void main(String[] args) {
        DRunnable dr = new DRunnable();
        Thread t = new Thread(dr);
        t.start();
        doingLongTime(2);
        t.interrupt();
    }
    public static class DRunnable implements Runnable{

        @Override
        public void run() {
            println("进入暂停。。。");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                println("收到中断异常。。。");
                println("做一些相关处理。。。");
            }
            println("继续执行或选择退出。。。");
        }
    }
}
