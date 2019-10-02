package com.org.hm.thread;

import java.util.concurrent.Phaser;

public class PhaserTest extends Base {
    //某个线程到达预设点后，可以选择等待同伴或自己退出，等大家都到达后，再一起向下一个预设点出发，随时都可以有新的线程加入，退出的也可以再次加入。
    static final int COUNT = 6;
    static Phaser ph = new Phaser() {
        protected boolean onAdvance(int phase, int registeredParties) {
            println("第(%d)局，剩余[%d]人", phase, registeredParties);
            return registeredParties == 0 ||
                    (phase != 0 && registeredParties == COUNT);
        };
    };

    public static void main(String[] args) throws Exception {
        new Thread(new Challenger("张三")).start();
        new Thread(new Challenger("李四")).start();
        new Thread(new Challenger("王五")).start();
        new Thread(new Challenger("赵六")).start();
        new Thread(new Challenger("大胖")).start();
        new Thread(new Challenger("小白")).start();
    }

    static class Challenger implements Runnable {
        String name;
        int state;

        Challenger(String name) {
            this.name = name;
            this.state = 0;
        }

        @Override
        public void run() {
            println("[%s]开始挑战。。。", name);
            ph.register();
            int phase = 0;
            int h;
            while (!ph.isTerminated() && phase < 100) {
                doingLongTime(1);
                if (state == 0) {
                    if (Decide.goon()) {
                        h = ph.arriveAndAwaitAdvance();
                        if (h < 0)
                            println("No%d.[%s]继续，但已胜利。。。", phase, name);
                        else
                            println("No%d.[%s]继续at(%d)。。。", phase, name, h);
                    } else {
                        state = -1;
                        h = ph.arriveAndDeregister();
                        println("No%d.[%s]退出at(%d)。。。", phase, name, h);
                    }
                } else {
                    if (Decide.revive()) {
                        state = 0;
                        h = ph.register();
                        if (h < 0)
                            println("No%d.[%s]复活，但已失败。。。", phase, name);
                        else
                            println("No%d.[%s]复活at(%d)。。。", phase, name, h);
                    } else {
                        println("No%d.[%s]没有复活。。。", phase, name);
                    }
                }
                phase++;
            }
            if (state == 0) {
                ph.arriveAndDeregister();
            }
            println("[%s]结束。。。", name);
        }

    }

    static class Decide {
        static boolean goon() {
            return random(9) > 4;
        }

        static boolean revive() {
            return random(9) < 5;
        }
    }
}
