package com.org.hm.thread;

import java.util.concurrent.Exchanger;

public class ExchangerTest extends Base {
    //两个线程在预设点交换变量，先到达的等待对方
    static Exchanger<Tool> ex = new Exchanger<Tool>();

    public static void main(String[] args) throws Exception {
        new Thread(new Staff("大胖", new Tool("笤帚", "扫地"), ex)).start();
        new Thread(new Staff("小白", new Tool("抹布", "擦桌"), ex)).start();
    }

    static class Staff implements Runnable {
        String name;
        Tool tool;
        Exchanger<Tool> ex;

        Staff(String name, Tool tool, Exchanger<Tool> ex) {
            this.name = name;
            this.tool = tool;
            this.ex = ex;
        }

        @Override
        public void run() {
            println("%s拿的工具是[%s]，他开始[%s]。。。", name, tool.name, tool.work);
            doingLongTime(1);
            println("%s开始交换工具。。。", name);
            try {
                tool = ex.exchange(tool);
            } catch (Exception e) {
                e.printStackTrace();
            }

            println("%s的工具变为[%s]，他开始[%s]。。。", name, tool.name, tool.work);
        }

    }

    static class Tool {
        String name;
        String work;

        Tool(String name, String work) {
            this.name = name;
            this.work = work;
        }
    }
}
