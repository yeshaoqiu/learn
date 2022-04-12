[TOC]

##1、Java程序天生就是多线程的
　　一个Java程序从main()方法开始执行，然后按照既定的代码逻辑执行，看似没有其他线程参与，但实际上Java程序天生就是多线程程序，因为执行main()方法的是一个名称为main的线程。

```
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

public class OnlyMain {
    public static void main(String[] args) {
        //Java 虚拟机线程系统的管理接口
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        // 不需要获取同步的monitor和synchronizer信息，仅仅获取线程和线程堆栈信息
        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(false, false);
        // 遍历线程信息，仅打印线程ID和线程名称信息
        for (ThreadInfo threadInfo : threadInfos) {
            System.out.println("[" + threadInfo.getThreadId() + "] " + threadInfo.getThreadName());
        }
    }
}
```
　　执行main方法，可以看到输出了6个线程信息

　　main线程：用户程序入口。

　　Reference Handler：清除Reference的线程。

　　Finalizer：调用对象finalize方法的线程。

　　Signal Dispatcher：分发处理发送给JVM信号的线程。

　　Attach Listener：内存dump，线程dump，类信息统计，获取系统属性等。

　　Monitor Ctrl-Break：监控Ctrl-Break中断信号的线程。

##2、线程的启动与中止
###2.1 启动线程
　　启动一个新线程有2种方式，这样说的原因是参考Thread类的说明：There are two ways to create a new thread of execution.

先看第一种：One is to declare a class to be a subclass of <code>Thread</code>。意思就是继承自Thread类，重写run()方法，然后new出一个对象，调用start()方法。
```
class PrimeThread extends Thread {
    long minPrime;

    PrimeThread(long minPrime) {
        this.minPrime = minPrime;
    }

    public void run() {

    }

    public static void main(String[] args) {
        PrimeThread p = new PrimeThread(143);
        p.start();
    }
}
```
　　再看第二种：The other way to create a thread is to declare a class that implements the <code>Runnable</code> interface。也就是说实现Runnable接口，然后实现run()方法，再把此类的实例作为一个参数初始化一个Thread实例，执行start()方法。

```
class PrimeRun implements Runnable {
    long minPrime;

    PrimeRun(long minPrime) {
        this.minPrime = minPrime;
    }

    public void run() {

    }

    public static void main(String[] args) {
        PrimeRun p = new PrimeRun(143);
        new Thread(p).start();
    }
}
```
###2.2 Thread和Runnable的区别
　　Thread才是Java里对线程的唯一抽象，Runnable只是对任务（业务逻辑）的抽象。Thread可以接受任意一个Runnable的实例并执行。

###2.3 线程的终止
（1）线程自然终止

　　要么是run执行完成了，要么是抛出了一个未处理的异常导致线程提前结束。

（2）stop

　　暂停、恢复和停止操作对应在线程Thread的API就是suspend()、resume()和stop()。但是这些API是过期的，也就是不建议使用的。不建议使用的原因主要有：以suspend()方法为例，在调用后，线程不会释放已经占有的资源（比如锁），而是占有着资源进入睡眠状态，这样容易引发死锁问题。同样，stop()方法在终结一个线程时不会保证线程的资源正常释放，通常是没有给予线程完成资源释放工作的机会，因此会导致程序可能工作在不确定状态下。正因为suspend()、resume()和stop()方法带来的副作用，这些方法才被标注为不建议使用的过期方法。

（3）中断

　　安全的终止则是其他线程通过调用某个线程A的interrupt()方法对其进行中断操作，中断好比其他线程对该线程打了个招呼，“A，你要中断了”，不代表线程A会立即停止自己的工作，同样的A线程完全可以不理会这种中断请求。因为java里的线程是协作式的，不是抢占式的。线程通过检查自身的中断标志位是否被置为true来进行响应。线程通过方法isInterrupted()来进行判断是否被中断，也可以调用静态方法Thread.interrupted()来进行判断当前线程是否被中断，不过Thread.interrupted()会同时将中断标识位改写为false。

　　如果一个线程处于了阻塞状态（如线程调用了sleep、join、wait等），则在线程在检查中断标志时如果发现中断标志为true，则会在这些阻塞方法调用处抛出InterruptedException异常，并且在抛出异常后会立即将线程的中断标示位清除，即重新设置为false。

　　不建议自定义一个取消标志位来中止线程的运行。因为run方法里有阻塞调用时会无法很快检测到取消标志，线程必须从阻塞调用返回后，才会检查这个取消标志。这种情况下，使用中断会更好，主要有以下2个原因：

① 一般的阻塞方法，如sleep等本身就支持中断的检查。

② 检查中断位的状态和检查取消标志位没什么区别，用中断位的状态还可以避免声明取消标志位，减少资源的消耗。

注意：处于死锁状态的线程无法被中断。

###2.4 run()和start()
　　Thread类是Java里对线程概念的抽象，可以这样理解：我们通过new Thread()其实只是new出一个Thread的实例，还没有和操作系统中真正的线程挂起钩来。只有执行了start()方法后，才实现了真正意义上的启动线程。start()方法让一个线程进入就绪队列等待分配cpu，分到cpu后才调用实现的run()方法，start()方法不能重复调用，如果重复调用会抛出异常，通过源码可以发现，start()方法先判断当前线程的状态，如果线程状态不为0，就会抛出异常。而run方法是业务逻辑实现的地方，本质上和任意一个类的任意一个成员方法并没有任何区别，可以重复执行，也可以被单独调用。

　　start()方法源码如下：

```
 public synchronized void start() {
        /**
         * This method is not invoked for the main method thread or "system"
         * group threads created/set up by the VM. Any new functionality added
         * to this method in the future may have to also be added to the VM.
         *
         * A zero status value corresponds to state "NEW".
         */
        if (threadStatus != 0)
            throw new IllegalThreadStateException();

        /* Notify the group that this thread is about to be started
         * so that it can be added to the group's list of threads
         * and the group's unstarted count can be decremented. */
        group.add(this);

        boolean started = false;
        try {
            start0();
            started = true;
        } finally {
            try {
                if (!started) {
                    group.threadStartFailed(this);
                }
            } catch (Throwable ignore) {
                /* do nothing. If start0 threw a Throwable then
                  it will be passed up the call stack */
            }
        }
    }
```
##3、其他基础API
###3.1 yield
　　yield()方法：使当前线程让出CPU占有权，但让出的时间是不可设定的。也不会释放锁资源。注意：因为又不是每个线程都需要这个锁的，而且执行yield( )的线程不一定就会持有锁。

所有执行yield()的线程有可能在进入到就绪状态后会被操作系统再次选中马上又被执行。

　　yield()方法在ConcurrentHashMap的initTable()方法中有用到，源码如下图所示：

```
private final Node<K,V>[] initTable() {
        Node<K,V>[] tab; int sc;
        while ((tab = table) == null || tab.length == 0) {
            if ((sc = sizeCtl) < 0)
                Thread.yield(); // lost initialization race; just spin
            else if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
                try {
                    if ((tab = table) == null || tab.length == 0) {
                        int n = (sc > 0) ? sc : DEFAULT_CAPACITY;
                        @SuppressWarnings("unchecked")
                        Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n];
                        table = tab = nt;
                        sc = n - (n >>> 2);
                    }
                } finally {
                    sizeCtl = sc;
                }
                break;
            }
        }
        return tab;
    }
```
　　sizeCtl小于0表示有其他线程在进行table的初始化，对于table的初始化工作，同一时间只能有一个线程执行，所以其他线程把CPU执行权让出,，提高CPU利用率。

##3.2 join
　　把指定的线程加入到当前线程，可以将两个交替执行的线程合并为顺序执行的线程。比如在线程B中调用了线程A的join()方法，直到线程A执行完毕后，才会继续执行线程B。

下面用一个隔壁老王舔到最后一无所有的故事来说明join的使用方法。有一天隔壁老王在食堂排队打饭，看到女神也来打饭，于是叫女神排在自己的前面，但是他万万没有想到的是，女神让她的男朋友排在女神前面，隔壁老王傻眼了。

```
public class JoinDemo {
    //隔壁老王的女神
    static class Goddess implements Runnable {
        //女神的男朋友
        private Thread gf;

        public Goddess(Thread gf) {
            this.gf = gf;
        }

        public void run() {
            System.out.println("女神开始排队打饭.....");
            try {
                System.out.println("女神让男朋友插队.....");
                gf.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(2000);//休眠2秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " 女神打饭完成.");
        }
    }

    //隔壁老王的女神的男朋友
    static class GoddessBoyfriend implements Runnable {
        public void run() {
            System.out.println("女神的男朋友开始排队打饭.....");
            try {
                Thread.sleep(2000);//休眠2秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " 女神的男朋友打饭完成.");
        }
    }

    public static void main(String[] args) throws Exception {
        //女神的男朋友
        GoddessBoyfriend goddessBoyfriend = new GoddessBoyfriend();
        Thread gbf = new Thread(goddessBoyfriend);

        //女神
        Thread goddess = new Thread(new Goddess(gbf));

        goddess.start();
        gbf.start();

        System.out.println("隔壁老王开始排队打饭.....");
        System.out.println("隔壁老王请女神插队.....");
        goddess.join();
        try {
            Thread.sleep(2000);//主线程休眠2秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " 隔壁老王打饭完成.");
    }
}
```
##4、线程优先级
　　在Java线程中，通过一个整型成员变量priority来控制优先级，优先级的范围从1~10，在线程构建的时候可以通过setPriority(int)方法来修改优先级，默认优先级是5，优先级高的线程分配时间片的数量要多于优先级低的线程。

　　设置线程优先级时，针对频繁阻塞（休眠或者I/O操作）的线程需要设置较高优先级，而偏重计算（需要较多CPU时间或者偏运算）的线程则设置较低的优先级，确保处理器不会被独占。在不同的JVM以及操作系统上，线程规划会存在差异，有些操作系统甚至会忽略对线程优先级的设定。所以线程的优先级在Java里一般不会特别设置，特别是不能把程序的正确运行寄托在线程的优先级上。

##5、守护线程
　　Daemon（守护）线程是一种支持型线程，因为它主要被用作程序中后台调度以及支持性工作。这意味着，当一个Java虚拟机中不存在非Daemon线程的时候，Java虚拟机将会退出。可以通过调用Thread.setDaemon(true)将线程设置为Daemon线程。我们一般用不上，比如垃圾回收线程就是Daemon线程。Daemon线程被用作完成支持性工作，但是在Java虚拟机退出时Daemon线程中的finally块并不一定会执行。在构建Daemon线程时，不能依靠finally块中的内容来确保执行关闭或清理资源的逻辑。

　　下面是一个守护线程的例子：

```
public class DaemonThread {
    private static class UseThread extends Thread {
        @Override
        public void run() {
            while (true) {
                System.out.println(Thread.currentThread().getName()
                        + " I am extends Thread.");
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        UseThread useThread = new UseThread();
        useThread.setDaemon(true);
        useThread.start();
        Thread.sleep(500);
    }
}
```
　　在主线程运行500毫秒后，主线程自然终止，因为useThread是守护线程，所以Java虚拟机退出。
