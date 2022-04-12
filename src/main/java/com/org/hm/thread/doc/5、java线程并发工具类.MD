[TOC]
##1、Fork-Join
###1.1 什么是Fork-Join
　　Java多线程的开发可以我们自己启用多线程，线程池，还可以使用forkjoin。forkjoin可以让我们不去了解诸如Thread、Runnable等相关的知识，只要遵循forkjoin的开发模式，就可以写出很好的多线程并发程序。

　　forkjoin采用的是分而治之。分而治之思想是：将一个难以直接解决的大问题，分割成一些规模较小的相同问题，以便各个击破，分而治之。分而治之的策略是：对于一个规模为n的问题，若该问题可以容易地解决（比如说规模n较小）则直接解决，否则将其分解为m个规模较小的子问题，这些子问题互相独立且与原问题形式相同(子问题相互之间有联系就会变为动态规范算法)，递归地解这些子问题，然后将各子问题的解合并得到原问题的解，这种算法设计策略叫做分治法。用一张图来表示forkjoin原理。

　　我们可以了解一下计算机的十大经典算法：快速排序、堆排序、归并排序 、二分查找、BFPRT（线性查找）、DFS（深度优先搜索）、BFS（广度优先搜索）、Dijkstra、动态规划、朴素贝叶斯分类。其中有哪一些用到的是分而治之呢？有3个，分别是快速排序、归并排序和二分查找。

　　归并排序是建立在归并操作上的一种有效的排序算法。该算法是采用分治法的一个非常典型的应用。将已有序的子序列合并，得到完全有序的序列；即先使每个子序列有序，再使子序列段间有序。若将两个有序表合并成一个有序表，称为2路归并，与之对应的还有多路归并。对于给定的一组数据，利用递归与分治技术将数据序列划分成为越来越小的半子表，在对半子表排序后，再用递归方法将排好序的半子表合并成为越来越大的有序序列。为了提升性能，有时我们在半子表的个数小于某个数（比如15）的情况下，对半子表的排序采用其他排序算法，比如插入排序。下面演示一下归并排序的过程。

###1.2 Fork-Join工作窃取
　　工作窃取是指当前线程的Task已经全被执行完毕，则自动取到其他线程的Task队列中取出Task继续执行。ForkJoinPool中维护着多个线程在不断地执行Task，每个线程除了执行自己职务内的Task之外，还会根据自己工作线程的闲置情况去获取其他繁忙的工作线程的Task，如此一来就能能够减少线程阻塞或是闲置的时间，提高CPU利用率。用一张图进行说明。


###1.3 Fork-Join使用
　　Fork-Join使用两个类来完成以上两件事情：ForkJoinTask和ForkJoinPool。我们要使用ForkJoin框架，必须首先创建一个ForkJoin任务。它提供在任务中执行fork和join的操作机制，通常我们不直接继承ForkjoinTask类，只需要直接继承其子类。

（1）RecursiveAction，用于没有返回结果的任务

（2）RecursiveTask，用于有返回值的任务

 task要通过ForkJoinPool来执行，使用submit 或 invoke 提交，两者的区别是：invoke是同步执行，调用之后需要等待任务完成，才能执行后面的代码；submit是异步执行。join()和get方法当任务完成的时候返回计算结果。调用get/join方法的时候会阻塞。还是用一个图来说明forkjoin的工作流程。



　　在我们自己实现的compute方法里，首先需要判断任务是否足够小，如果足够小就直接执行任务。如果不足够小，就必须分割成两个子任务，每个子任务在调用invokeAll方法时，又会进入compute方法，看看当前子任务是否需要继续分割成孙任务，如果不需要继续分割，则执行当前子任务并返回结果。使用join方法会等待子任务执行完并得到其结果。

###1.4 Fork-Join VS 单线程
　　假设有一个业务场景，数据库中有编号为0到1千万的会员信息，要统计所有会员的余额总和。为了对比结果的一致性，用户的余额不用随机数表示，就用编号代表用户的余额。现在的做法是每次从数据库查询出5000条数据进行统计，直到所有数据统计完成，进行汇总。对比看看单线程和Fork-Join的差异。

先看单线程场景：

```
public class SingleThreadSumNumber {
    /**
     * 每次查询5000条进行统计
     */
    private static final int THRESHOLD = 5000;

    /**
     * 最小值
     */
    private static final int MIN = 0;

    /**
     * 最大值
     */
    private static final int MAX = 10000000;

    public void sumNumber() {
        long sum = 0;
        long startTime = System.currentTimeMillis();
        int start = MIN;
        int end = MIN + THRESHOLD;

        boolean isFirstTime = true;
        while (end <= MAX) {
            sum = sum + batchSum(start, end);
            if (isFirstTime) {
                start = start + THRESHOLD + 1;
                isFirstTime = false;
            } else {
                start = start + THRESHOLD;
            }
            end = end + THRESHOLD;
        }
        System.out.println("The result is " + sum
                + " spend time:" + (System.currentTimeMillis() - startTime) + "ms");
    }

    /**
     * 统计每次查询出来的余额总和
     * @param start
     * @param end
     * @return
     */
    public long batchSum(int start, int end) {
        long sum = 0;
        try {
            Thread.sleep(15);//休眠15毫秒模拟查询业务
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = start; i <= end; i++) {
            sum += i;
        }
        return sum;
    }

    public static void main(String[] args) {
        SingleThreadSumNumber thread = new SingleThreadSumNumber();
        thread.sumNumber();
    }
}
```
运行程序输出以下结果：


余额总和为50000005000000，花费了30119毫秒，下面使用forkjoin来进行统计：

```
 1 import java.util.concurrent.ForkJoinPool;
 2 import java.util.concurrent.RecursiveTask;
 3 
 4 public class ForkJoinDemo {
 5     /**
 6      * 门限值，如果任务门限低于此值，则进行计算
 7      */
 8     private static final int THRESHOLD = 5000;
 9 
10     /**
11      * 最小值
12      */
13     private static final int MIN = 0;
14 
15     /**
16      * 最大值
17      */
18     private static final int MAX = 10000000;
19 
20     private static class SumNumberTask extends RecursiveTask<Long> {
21         private int start;
22         private int end;
23 
24         public SumNumberTask(int start, int end) {
25             this.start = start;
26             this.end = end;
27         }
28 
29         @Override
30         protected Long compute() {
31             if (end - start < THRESHOLD) {
32                 return sumBatch(start, end);
33             } else {
34                 int mid = (start + end) / 2;
35                 SumNumberTask left = new SumNumberTask(start, mid);
36                 SumNumberTask right = new SumNumberTask(mid + 1, end);
37                 invokeAll(left, right);
38                 long leftResult = left.join();
39                 long rightResult = right.join();
40                 return leftResult + rightResult;
41             }
42         }
43     }
44 
45     public void sumNumber() {
46         ForkJoinPool pool = new ForkJoinPool();
47         long start = System.currentTimeMillis();
48         int recordMin = MIN;
49         int recordMax = MAX;
50         SumNumberTask sumTask = new SumNumberTask(recordMin, recordMax);
51         pool.invoke(sumTask);
52         System.out.println("Task is Running.....");
53         Long result = sumTask.join();
54         System.out.println("The result is " + result + " spend time:"
55                 + (System.currentTimeMillis() - start) + "ms");
56     }
57 
58     /**
59      * 统计每次任务的总和
60      * @param fromId
61      * @param toId
62      * @return
63      */
64     public static long sumBatch(int fromId, int toId) {
65         long sum = 0;
66         try {
67             Thread.sleep(15);//休眠15毫秒模拟查询业务
68         } catch (InterruptedException e) {
69             e.printStackTrace();
70         }
71         for (int i = fromId; i <= toId; i++) {
72             sum += i;
73         }
74         return sum;
75     }
76 
77     public static void main(String[] args) {
78         ForkJoinDemo forkJoinDemo = new ForkJoinDemo();
79         forkJoinDemo.sumNumber();
80     }
81 }
```
输出结果：


　　余额总和为50000005000000，和使用单线程统计时一致，使用forkjoin达到了同样的目的，但是只花费了4078毫秒，性能提升了7倍多。为了使性能有进一步提升，我们可以在第44行指定并发数量。不传参情况下，默认并发量是当前服务器的逻辑CPU个数。我们把并发量调整成64，即ForkJoinPool pool = new ForkJoinPool(16 * 4)，执行程序，输出结果为：


统计结果一致，花费了567毫秒，比起单线程统计，性能提升了53倍之多，由此可见forkjoin的并发威力。

##2、CountDownLatch
###2.1 什么是CountDownLatch
　　JDK对CountDownLatch的解释是：一种同步辅助器，它允许一个或多个线程等待，直到在其他线程中执行的一组操作完成为止。举个例子来理解CountDownLatch：隔壁寝室的老王今天要参加学校运动会的400米决赛，跟小王一起争夺冠军的还有另外5个人，不管这6位选手的内心多激动多澎湃，也要等裁判的发令枪响了之后才能起跑，裁判不发出指令，选手就只能在起跑线等待，这就是CountDownLatch的作用。但是实际场景并不只有一个发令裁判，参加过学校运动会的同学都知道，还可能需要若干个裁判进行手动计时，要等所有的裁判都就位后，发令枪一响，运动员才能起跑。假设有3个计时裁判，一个发令裁判，用一个图来说明。

　　在比赛开始前，发令裁判会用洪荒之力吼一声，各~就~各~位，此时发令裁判会用炯炯有神的目光和3位计时裁判交流，3位裁判分别点头示意已经准备好了，此时发令裁判会再次大吼一声，预备~~~跑！！！此时憋了许久的6位运动员飞奔出去，当然老王遥遥领先，毕竟女神给他说了跑第一名的话晚上有奖励。发令裁判的任务完成，不用继续执行下去，而3个计时裁判继续工作，对6位选手的成绩进行一个记录。

###2.1 CountDownLatch实战
用一段程序来模拟老王参加运动会400米决赛的场景。

```
import java.util.concurrent.CountDownLatch;

public class CountDownLatchDemo {

    /**
     * 运动员在计时裁判和发令裁判就位后才能起跑
     */
    static CountDownLatch sportsManLatch = new CountDownLatch(4);

    /**
     * 发令裁判在3个计时裁判准备好之后才能发令
     */
    static CountDownLatch orderRefereeLatch = new CountDownLatch(3);

    /**
     * 计时裁判
     */
    static class TimeReferee implements Runnable {
        private int no;

        public TimeReferee(int no) {
            this.no = no;
        }

        @Override
        public void run() {
            System.out.println(no + "号计时裁判就位");
            orderRefereeLatch.countDown();
            sportsManLatch.countDown();
        }
    }

    /**
     * 发令裁判
     */
    static class OrderReferee implements Runnable {
        @Override
        public void run() {
            try {
                orderRefereeLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("发令裁判发出指令~~~~~~");
            sportsManLatch.countDown();
        }
    }

    /***
     * 运动员
     */
    static class SportsMan implements Runnable {
        private int no;

        public SportsMan(int no) {
            this.no = no;
        }

        @Override
        public void run() {
            try {
                System.out.println(no + "号运动员已经就位");
                sportsManLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(no + "号选手说，我要跑第一");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        //6个运动员就位
        for (int i = 0; i < 6; i++) {
            new Thread(new SportsMan(i)).start();
        }

        //发令裁判和计时裁判眼神确认，等计时裁判都准备好之后发令
        new Thread(new OrderReferee()).start();

        //3个计时裁判就位
        for (int i = 0; i < 3; i++) {
            new Thread(new TimeReferee(i)).start();
        }
    }
}
```
程序输出：


##3、CyclicBarrier
###3.1 什么是CyclicBarrier
　　JDK对CyclicBarrier的解释是：一种同步辅助工具，它允许一组线程全部互相等待以到达一个公共的障碍点。我们可以从字面意思理解它，可循环使用（Cyclic）的屏障（Barrier）。它要做的事情是，让一组线程到达一个屏障（也可以叫同步点）时被阻塞，直到最后一个线程到达屏障时，屏障才会打开，所有被屏障拦截的线程才能继续运行。CyclicBarrier默认的构造方法是CyclicBarrier（int parties），parties表示屏障拦截的线程数量，每个线程调用await方法告诉CyclicBarrier我已经到达了屏障，然后当前线程被阻塞。CyclicBarrier还提供一个更高级的构造函数CyclicBarrier（int parties，Runnable barrierAction），用于在parties个线程到达屏障时，优先执行barrierAction，方便处理更复杂的业务场景。还用一张图来说明。



###3.2 CyclicBarrier实战
　　CyclicBarrier可以用于多线程计算数据，最后合并计算结果的场景。我们模拟3个子线程向一个map中添加数据，它们添加数据完成后，到一个屏障点进行等待，由统计线程对数据进行打印，统计线程工作结束后，3个子线程再被统一释放去干其他工作。我们设置2个屏障点来演示,，体现其可循环使用的特征。

```
public class CyclicBarrierDemo {
    private static CyclicBarrier barrier = new CyclicBarrier(3, new CollectThread());

    /**存放子线程产生数据的容器*/
    private static ConcurrentHashMap<String, Long> map = new ConcurrentHashMap<>();

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 3; i++) {
            Thread thread = new Thread(new WorkThread());
            thread.start();
        }
        Thread.sleep(5);
    }

    /**
     * 负责对子线程的结果进行其他处理
     */
    private static class CollectThread implements Runnable {
        @Override
        public void run() {
            StringBuilder result = new StringBuilder();
            for (Map.Entry<String, Long> workResult : map.entrySet()) {
                result.append("[" + workResult.getValue() + "]");
            }
            System.out.println("the result = " + result);
            System.out.println("CollectThread do other things");
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("CollectThread end........");
        }
    }

    /**
     * 实际工作的子线程
     */
    private static class WorkThread implements Runnable {
        @Override
        public void run() {
            long id = Thread.currentThread().getId();
            map.put(id + "", id);
            Random r = new Random();
            try {
                Thread.sleep(r.nextInt(1000));
                System.out.println("Thread_" + id + " first do something ");
                //第一次到达屏障
                barrier.await();
                System.out.println("Thread_" + id + " first do other things");

                Thread.sleep(r.nextInt(500));
                map.put(id * 2 + "", id * 2);
                System.out.println("Thread_" + id + " second do something ");
                //第二次到达屏障
                barrier.await();
                System.out.println("Thread_" + id + " second other things ");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
```
程序输出：


###3.3 CountDownLatch和CyclicBarrier对比
　　CountDownLatch的计数器只能使用一次，而CyclicBarrier的计数器可以反复使用。CountDownLatch.await()一般阻塞工作线程，所有的进行预备工作的线程执行countDown()，而CyclicBarrier通过工作线程调用await()从而自行阻塞，直到所有工作线程达到指定屏障，再大家一起往下走。在控制多个线程同时运行上，CountDownLatch可以不限线程数量，而CyclicBarrier是固定线程数。同时，CyclicBarrier还可以提供一个barrierAction，合并多线程计算结果。

##4、Callable、Future和FutureTask
###4.1 Runnable、Callable、Future和FutureTask之间的关系
　　Runnable是一个接口，在它里面只声明了一个run()方法，由于run()方法返回值为void类型，所以在执行完任务之后无法返回任何结果。Callable位于java.util.concurrent包下，它也是一个接口，在它里面也只声明了一个方法，只不过这个方法叫做call()，这是一个泛型接口，call()函数返回的类型就是传递进来的V类型。Future就是对于具体的Runnable或者Callable任务的执行结果进行取消、查询是否完成、获取结果。要获取返回结果时可以调用get方，该方法会阻塞直到任务返回结果。因为Future只是一个接口，所以是无法直接用来创建对象使用的，因此就有了FutureTask。FutureTask类实现了RunnableFuture接口，RunnableFuture继承了Runnable接口和Future接口，所以它既可以作为Runnable被线程执行，又可以作为Future得到Callable的返回值。用一个图来说明。


　　因此当我们想通过一个线程运行Callable，但是Thread不支持构造方法中传递Callable的实例，我们需要通过FutureTask把一个Callable包装成Runnable，然后再通过这个FutureTask拿到Callable运行后的返回值。要想new出一个FutureTask的实例，有2种方式，直接贴出代码。

```
    public FutureTask(Callable<V> callable) {
        if (callable == null)
            throw new NullPointerException();
        this.callable = callable;
        this.state = NEW;       // ensure visibility of callable
    }


    public FutureTask(Runnable runnable, V result) {
        this.callable = Executors.callable(runnable, result);
        this.state = NEW;       // ensure visibility of callable
    }
```
###4.2 Callable和FutureTask实战
　　这个例子比较简单，在一个主线程中创建一个callable来对1到10000进行累加，再休眠3秒，然后把这个callable封装成一个futureTask，交给一个线程去运行，最终查看callable的返回结果和阻塞效果。

```
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class FutureTaskDemo {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Callable<Long> callable = new Callable<Long>() {
            long sum = 0;

            @Override
            public Long call() throws Exception {
                for (int i = 0; i <= 10000; i++) {
                    sum += i;
                }
                Thread.sleep(3000);//主要是为了演示get()时候的阻塞效果
                return sum;
            }
        };
        FutureTask<Long> futureTask = new FutureTask<>(callable);
        new Thread(futureTask).start();
        Thread.sleep(10);
        System.out.println("main线程继续执行");
        System.out.println("获取callable计算结果 = " + futureTask.get());
        System.out.println("main线程继续执行 ");
    }
}
```
程序输出：

可以看到当futureTask.get()没有获取到返回结果时，主线程是处于阻塞状态。

###4.3 手写一个FutureTask
　　要实现一个简易的FutureTask，通过上面对几个接口之间关系的介绍，以及阅读FutureTask代码可以看出，只需定义一个类，实现Runnable和Future接口，并实现run()方法和get()方法就可以了，核心思想就是上一篇文章中提到的通知/等待机制。直接上代码：

```
import java.util.concurrent.*;

public class MyFutureTask<V> implements Runnable, Future<V> {
    private Callable<V> callable;

    private V result = null;

    public MyFutureTask(Callable<V> callable) {
        this.callable = callable;
    }

    @Override
    public void run() {
        V temp = null;
        try {
            temp = callable.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        synchronized (this) {
            result = temp;
            this.notifyAll();
        }
    }

    @Override
    public V get() throws InterruptedException {
        if (result != null) {
            return result;
        }
        System.out.println("等待结果执行完成。。。。。");
        synchronized (this) {
            this.wait();
        }
        return result;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }


    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }
}
```
 为了验证效果，把上一段代码中的FutureTask改成MyFutureTask，其余代码都不变。

```
import java.util.concurrent.Callable;

public class FutureTaskDemo {
    public static void main(String[] args) throws InterruptedException {
        Callable<Long> callable = new Callable<Long>() {
            long sum = 0;

            @Override
            public Long call() throws Exception {
                for (int i = 0; i <= 10000; i++) {
                    sum += i;
                }
                Thread.sleep(3000);//主要是为了演示get()时候的阻塞效果
                return sum;
            }
        };
        MyFutureTask<Long> futureTask = new MyFutureTask<>(callable);
        new Thread(futureTask).start();
        Thread.sleep(10);
        System.out.println("main线程继续执行");
        System.out.println("获取callable计算结果 = " + futureTask.get());
        System.out.println("main线程继续执行 ");
    }
}
```
运行程序，可以看到输出结果和阻塞现象与使用FutureTask一致：

