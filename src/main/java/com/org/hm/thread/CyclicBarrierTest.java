package com.org.hm.thread;

import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierTest extends Base {
    //某个线程到达预设点时就在此等待，等所有的线程都到达时，大家再一起向下个预设点出发。如此循环反复下去
    static final int COUNT = 5;
    static CyclicBarrier cb = new CyclicBarrier(COUNT, new Singer());

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < COUNT; i++) {
            new Thread(new Staff(i, cb)).start();
        }
    }

    static class Singer implements Runnable {
        @Override
        public void run() {
            println("为大家唱歌。。。");
        }

    }

    static class Staff implements Runnable {
        CyclicBarrier cb;
        int num;

        Staff(int num, CyclicBarrier cb) {
            this.num = num;
            this.cb = cb;
        }

        @Override
        public void run() {
            println("员工(%d)出发。。。", num);
            doingLongTime(1);
            println("员工(%d)到达地点一。。。", num);
            try {
                cb.await();
            } catch (Exception e) {
                e.printStackTrace();
            }

            println("员工(%d)再出发。。。", num);
            doingLongTime(2);
            println("员工(%d)到达地点二。。。", num);
            try {
                cb.await();
            } catch (Exception e) {
                e.printStackTrace();
            }

            println("员工(%d)再出发。。。", num);
            doingLongTime(1);
            println("员工(%d)到达地点三。。。", num);
            try {
                cb.await();
            } catch (Exception e) {
                e.printStackTrace();
            }

            println("员工(%d)结束。。。", num);
        }

    }
}
