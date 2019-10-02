package com.org.hm.thread;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchTest extends Base {
    //每完成一个线程，计数器减1，当减到0时，被阻塞的线程自动执行
    static final int COUNT = 20;
    static CountDownLatch cdl = new CountDownLatch(COUNT);

    public static void main(String[] args) throws Exception {
        new Thread(new Teacher(cdl)).start();

        doingLongTime(1);

        for (int i = 0; i < COUNT; i++) {
            new Thread(new Student(i, cdl)).start();
        }

    }

    static class Teacher implements Runnable {
        CountDownLatch cdl;

        Teacher(CountDownLatch cdl) {
            this.cdl = cdl;
        }

        @Override
        public void run() {
            println("老师发卷子。。。");
            try {
                cdl.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            println("老师收卷子。。。");
        }
    }

    static class Student implements Runnable {
        CountDownLatch cdl;
        int num;

        Student(int num, CountDownLatch cdl) {
            this.num = num;
            this.cdl = cdl;
        }

        @Override
        public void run() {
            println("学生(%d)写卷子。。。", num);
            doingLongTime(1);
            println("学生(%d)交卷子。。。", num);
            cdl.countDown();
        }
    }
}
