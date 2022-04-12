[POC]

##1、为什么线程之间需要协作
　　线程之间相互配合，完成某项工作，比如：一个线程修改了一个对象的值，而另一个线程感知到了变化，然后进行相应的操作，整个过程开始于一个线程，而最终执行又是另一个线程。前者是生产者，后者就是消费者，这种模式隔离了“做什么”（What）和“怎么做”（How）。简单的办法是让消费者线程不断地循环检查变量是否符合预期，在while循环中设置不满足的条件，如果条件满足则退出while循环，从而完成消费者的工作。这样进行线程之间的协作却存在如下2个问题：

（1）难以确保及时性。

（2）难以降低开销。如果降低睡眠的时间，比如休眠1毫秒，这样消费者能更加迅速地发现条件变化，但是却可能消耗更多的处理器资源，造成了无端的浪费。

那么有没有什么办法可以解决以上2个问题呢？此时等待/通知机制毫不客气的站出来说，都让开，交给我，我能行！　　

##2、等待/通知机制
###2.1 等待/通知机制介绍
等待/通知机制是指一个线程A调用了对象obejct的wait()方法进入等待状态，而另一个线程B调用了对象obejct的notify()或者notifyAll()方法，线程A收到通知后从对象obejct的wait()方法返回，进而执行后续操作。上述两个线程通过对象obejct来完成交互，而对象上的wait()和notify/notifyAll()的关系就如同开关信号一样，用来完成等待方和通知方之间的交互工作。

notify()：

通知一个在对象上等待的线程，使其从wait()方法返回，而返回的前提是该线程获取到了对象的锁。哪个线程能得到通知是随机的，不能指定。

notifyAll()：

通知所有等待在该对象上的线程，这些线程会去竞争对象锁，得到锁的某一个线程可以继续执行wait()后的逻辑。

wait():

调用该方法的线程进入 WAITING状态，只有等待另外线程的通知或被中断才会返回。需要注意，调用wait()方法后，会释放对象的锁。

wait(long):

超时等待一段时间，这里的参数时间是毫秒，也就是等待长达n毫秒，如果没有通知就超时返回。

wait (long,int):

对于超时时间更细粒度的控制，可以达到纳秒。

###2.1 等待/通知机制使用的标准范式
等待方遵循如下原则：

（1）获取对象的锁。

（2）如果条件不满足，那么调用对象的wait()方法，被通知后仍要检查条件。

（3）条件满足则执行对应的逻辑。

用一段伪代码表示：

    synchronized (对象) {
        while (条件不满足) {
            对象.wait();
        }
        对应逻辑处理
    }
通知方遵循如下原则：

（1）获得对象的锁。

（2）改变条件。

（3）通知所有等待在对象上的线程。

（4）通知方法放在同步代码块的最后一行。

用一段伪代码表示：

    synchronized (对象) {
        改变条件
        对象.notifyAll();   
    }
　　在调用wait()、notify()和notifyAll()方法之前，线程必须要获得该对象的对象锁，即只能在同步方法或同步块中调用wait()方法、notify()和notifyAll()方法。调用wait()方法后，当前线程释放锁， 执行notify()和notifyAll()方法的线程退出synchronized代码块的时候，假设是执行的notifyAll()，会唤醒所有处于等待的线程，这些线程会去竞争对象锁。如果其中一个线程A获得了该对象锁，线程A就会继续往下执行，其余被唤醒的线程处于阻塞状态。在线程A退出synchronized代码块释放锁后，其余已经被唤醒的处于阻塞状态的线程将会继续竞争该锁，一直进行下去，直到所有被唤醒的线程都执行完毕。

###2.2 notify()和notifyAll()应该用谁
　　尽可能用notifyAll()，谨慎使用notify()，因为notify()只会唤醒一个线程，我们无法确保被唤醒的这个线程一定就是我们需要唤醒的线程。

###2.3 手写一个数据库连接池
　　我们在使用数据库连接池的时候，当某一个线程超过配置的最大等待时长还没有拿到连接时，就会报出异常。我们使用等待/通知机制来模拟一个数据库连接池。分别定义连接类、连接池实现类和测试类。

连接类：

```
import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public class MySqlConnection implements Connection {
    public static final Connection  createConnection(){
        return new MySqlConnection();
    }
    //todo 其余接口使用默认实现，这里就不一一给出。
}
```
连接池实现类：

```
import java.sql.Connection;
import java.util.LinkedList;

public class MyConnectionPool {
    /**装连接的容器*/
    private static LinkedList<Connection> pool = new LinkedList<>();

    /**
     * 初始化连接池
     * @param poolSize
     */
    public MyConnectionPool(int poolSize){
        if(poolSize > 0){
            for (int i = 0; i < poolSize; i++) {
                pool.add(MySqlConnection.createConnection());
            }
        }
    }

    /**
     * 释放一个连接
     * @param connection
     */
    public void releaseConnection(Connection connection){
        if(connection != null){
            synchronized (pool){
                pool.add(connection);
                pool.notifyAll();
            }
        }
    }

    /**
     * 获取一个连接
     * @param millions  超时时间
     * @return
     */
    public Connection getConnection(long millions){
        synchronized (pool){
            if(millions <= 0){
                while (pool.isEmpty()){
                    try {
                        pool.wait();
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                return pool.removeFirst();
            }
            else {
                //计算超时时刻
                long overTime = System.currentTimeMillis() + millions;
                //剩余等待时长
                long remaining = millions;
                //当剩余等待时间大于0并且连接池为空，就等待
                while (remaining > 0 && pool.isEmpty()){
                    try {
                        pool.wait();
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    //被唤醒后重新计算剩余等待时长
                    remaining = overTime - System.currentTimeMillis();
                }
                Connection result = null;
                if(!pool.isEmpty()){
                    result = pool.removeFirst();
                }
                return result;
            }
        }
    }
}
```
测试类：

```
import java.sql.Connection;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class MyConnectionPoolTest {
    /**初始化10个数据库连接*/
    static MyConnectionPool pool = new MyConnectionPool(10);

    /**等待时长为1秒*/
    static long maxWait = 1000;

    /**获取连接的线程数量*/
    static int threadNumber = 500;

    /**所有获取连接线程执行完成后再执行main线程*/
    static CountDownLatch mainCountDownLatch = new CountDownLatch(threadNumber);

    /**保证所有获取连接线程同时执行*/
    static CountDownLatch workCountDownLatch = new CountDownLatch(threadNumber);

    static class Worker implements Runnable{
        AtomicInteger success;
        AtomicInteger fail;

        public Worker(AtomicInteger success, AtomicInteger fail){
            this.success = success;
            this.fail = fail;
        }

        @Override
        public void run() {
           try {
               workCountDownLatch.await();
               Connection connection = pool.getConnection(maxWait);
               if(connection != null){
                   try{
                       Statement statement = connection.createStatement();
                       Thread.sleep(30);//休眠30毫秒模拟实际业务
                       connection.commit();
                   }finally {
                       pool.releaseConnection(connection);
                       success.getAndAdd(1);
                   }
               }
               else {
                   fail.getAndAdd(1);
                   System.out.println(Thread.currentThread().getName() + "等待超时，没有拿到连接！");
               }
           }catch (Exception e){
                e.printStackTrace();
           }
           mainCountDownLatch.countDown();
        }
    }

    public static void main(String[] args) {
        AtomicInteger success = new AtomicInteger(0);//记录成功次数
        AtomicInteger fail = new AtomicInteger(0);//记录失败次数
        for (int i = 0; i < threadNumber; i++) {
            new Thread(new Worker(success, fail)).start();
            workCountDownLatch.countDown();
        }
        try {
            mainCountDownLatch.await();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        System.out.println("总共尝试获取连接次数：" + threadNumber);
        System.out.println("成功次数：" + success.get());
        System.out.println("失败次数：" + fail.get());
    }
}
```
运行程序，从输出结果可以看出，通过通知/等待超时模式成功的实现了一个简易的数据库连接池。



###2.4 常见面试题
调用yield() 、sleep()、wait()、notify()等方法对锁有何影响？

答：yield() 、sleep()被调用后，都不会释放当前线程所持有的锁。

调用wait()方法后，会释放当前线程持有的锁，而且当前线程被唤醒后，会重新去竞争锁，得到锁到后才会执行wait()方法后面的代码。

调用notify()系列方法后，对锁无影响，线程只有在synchronized同步代码执行完后才会自然而然的释放锁，所以notify()系列方法一般都是synchronized同步代码的最后一行。
