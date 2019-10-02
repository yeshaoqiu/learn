package com.org.hm.thread;

public class WaitNotify extends Base {
    public static void main(String[] args) {
        BRunnable b = new BRunnable();
        new Thread(b).start();
        b.tellToPause();
        doingLongTime(8);
        b.tellToResume();
    }

    public static class BRunnable implements Runnable{
        private volatile boolean pause;

        void tellToPause() {
            pause = true;
        }

        void tellToResume() {
            synchronized (this) {
                this.notify();
            }
        }

        @Override
        public void run() {
            println("进入不可暂停区域 1。。。");
            doingLongTime(5);
            println("退出不可暂停区域 1。。。");
            println("检测标志pause = %s", String.valueOf(pause));
            if (pause) {
                println("暂停执行");
                try {
                    synchronized (this) {
                        this.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                println("恢复执行");
            }
            println("进入不可暂停区域 2。。。");
            doingLongTime(5);
            println("退出不可暂停区域 2。。。");
        }
    }
}
