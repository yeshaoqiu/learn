package com.org.hm.thread;

public class Join extends Base {
    //join方法可以让某个线程插到自己前面，等它执行完，自己才会继续执行
    public static void main(String[] args) {
        CRunnable cr = new CRunnable();
        Thread t = new Thread(cr);
        t.start();

        doingLongTime(1);

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        println("终于轮到我了");
    }

    public static class CRunnable implements Runnable{

        @Override
        public void run() {
            println("进入不可暂停区域 1。。。");
            doingLongTime(5);
            println("退出不可暂停区域 1。。。");
        }
    }
}
