[TOC]
##1、什么是原子操作？
　　所谓原子操作是指不会被线程调度机制打断的操作，这种操作一旦开始，就一直运行到结束，中间不会有任何线程上下文切换。原子操作可以是一个步骤，也可以是多个操作步骤，但是其顺序不可以被打乱，也不可以被切割而只执行其中的一部分。我们常用的i++看起来虽然简单，但这并不是一个原子操作，具体原理后面单独介绍。假定有两个操作A和B，如果从执行A的线程来看，当另一个线程执行B时，要么将B全部执行完，要么完全不执行B，那么A和B对彼此来说是原子的。将整个操作视作一个整体是原子性的核心特征。

##2、如何实现原子操作？
###2.1 锁机制实现原子操作及其问题
实现原子操作可以使用锁。锁机制满足基本的需求是没有问题的，但是有的时候我们的需求并非这么简单，我们需要更有效，更加灵活的机制。synchronized关键字是基于阻塞的锁机制，也就是说当一个线程拥有锁的时候，访问同一资源的其它线程需要等待，直到该线程释放锁。使用synchronized关键字存在这样的问题：

（1）如果被阻塞的线程优先级很高很重要怎么办？

（2）如果获得锁的线程一直不释放锁怎么办？

（3）如果有大量的线程来竞争资源，那CPU将会花费大量的时间和资源来处理这些竞争，同时，还有可能出现一些例如死锁之类的情况。

使用锁机制是一种比较粗糙、粒度比较大的机制，我们可以想象多个线程操作同一个计数器的业务场景，使用锁机制的话显得太过笨重。

###2.2 CAS机制
　　实现原子操作还可以使用当前的处理器基本都支持CAS(Compare And Swap)的指令，CPU指令集上提供了CAS操作相关指令，实现原子操作可以使用这些指令。每一个CAS操作过程都包含3个运算参数：一个内存地址V，一个期望的值A和一个新值B，操作的时候如果这个地址上存放的值等于这个期望的值A，则将地址上的值赋为新值B，否则不做任何操作。

###2.3 CAS使用
　　先来模拟一个多个线程操作同一个计数器的场景，JDK中提供了boolean、int和long基本类型对应的原子包装类AtomicBoolean、AtomicInteger和AtomicLong。我们用AtomicInteger演示，通过CountDownLatch进行并发模拟，如果对CountDownLatch用法不了解，欢迎查看上一篇文章，有通俗易懂的例子。先对AtomicInteger的主要API做一个介绍：

（1）int addAndGet（int delta）：以原子方式将输入的数值与实例中的值（AtomicInteger里的value）相加，并返回结果。

（2）boolean compareAndSet（int expect，int update）：如果当前数值等于expect，则以原子方式将当前值设置为update。

（3）int getAndIncrement()：以原子方式将当前值加1，注意，这里返回的是自增前的值。

（4）int getAndSet（int newValue）：以原子方式设置为newValue的值，并返回旧值。

```
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicIntegerDemo {
    static AtomicInteger counter = new AtomicInteger(0);

    static CountDownLatch countDownLatch = new CountDownLatch(20);

    static class CounterThread implements Runnable {
        @Override
        public void run() {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            counter.getAndIncrement();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 20; i++) {
            Runnable thread = new CounterThread();
            new Thread(thread).start();
            countDownLatch.countDown();
        }
        Thread.sleep(2000); //保证子线程全部执行完成
        System.out.println("20个线程并发执行getAndIncrement()方法后的结果：" + counter.get());
        counter.compareAndSet(20, 18);//如果counter当前数值为20，则以原子方式更新为18
        System.out.println("compareAndSet(20, 18)后的结果：" + counter.get());
    }
}
```
程序中模拟了20个线程并发对一个计数器进行自增操作，结果输出为20，可以看到这段代码并没有用任何的锁，也达到了原子操作目的。


###2.4 CAS原理
　　CAS的基本思路就是，如果内存地址V上的值和期望的值A相等，则给其赋予新值B，否则不做任何事儿。CAS就是在一个循环里不断的做CAS操作，直到成功为止。CAS是怎么实现线程的安全呢？语言层面不做处理，JDK 调用这些指令来完成CAS操作，本质上就是将其交给CPU和内存，利用CPU的多处理能力，实现硬件层面的阻塞，再加上volatile变量的特性即可实现基于原子操作的线程安全。用一张图来说明。

##3、CAS实现原子操作的三大问题
###3.1 ABA问题
　　因为CAS需要在操作值的时候，检查值有没有发生变化，如果没有发生变化则更新，但是如果一个值原来是A，变成了B，又变成了A，那么使用CAS进行检查时会发现它的值没有发生变化，但是实际上却变化了。举个通俗易懂的例子，我的同事老王今年35岁了，还没有女朋友，我问他有什么要求，给他介绍一个女朋友。老王就说了，只要是没有结婚、35岁以下的女的就行。于是我就给他介绍了一个28岁，刚刚离婚不久的女同志，他还感谢了我好久，可能是他现在都还不知道他这个女朋友离过婚。这就是典型的ABA问题，只关心当前状态，而不管中间经历了什么。ABA问题的解决思路就是使用版本号。给变量追加一个版本号，每次变量更新的时候把版本号加1，那么A→B→A就会变成1A→2B→3A。就好比老王的要求改成：35岁以下，没有结婚并且离婚次数为0的女性，就不会发生刚刚的事情了。

###3.2 循环时间长开销大。
　　CAS自旋如果长时间不成功，会给CPU带来非常大的执行开销。

###3.3 只能保证一个共享变量的原子操作
　　当对一个共享变量执行操作时，我们可以使用循环CAS的方式来保证原子操作，但是对多个共享变量操作时，循环CAS就无法保证操作的原子性，这个时候就可以用锁。怎么解决这个问题呢？从Java 1.5开始，JDK提供了AtomicReference类来保证引用对象之间的原子性，就可以把多个变量放在一个对象里来进行CAS操作。

##4、JDK中相关原子操作类
###4.1 AtomicReference
　　AtomicReference，可以原子更新的对象引用。AtomicReference有一个compareAndSet()方法，它可以将已持有引用与预期引用进行比较，如果它们相等，则在AtomicReference对象内设置一个新的引用。看一段代码：

```
import java.util.concurrent.atomic.AtomicReference;

public class AtomicReferenceDemo {
    static AtomicReference<UserInfo> atomicReference;

    public static void main(String[] args) {
        //原引用
        UserInfo oldUser = new UserInfo("老王", 35);
        atomicReference = new AtomicReference<>(oldUser);

        //新引用
        UserInfo updateUser = new UserInfo("小宋", 21);
        atomicReference.compareAndSet(oldUser, updateUser);

        System.out.println("使用compareAndSet()替换原有引用后的结果：" + atomicReference.get());
        System.out.println("原引用：" + oldUser);
    }

    static class UserInfo {
        private String name;
        private int age;

        public UserInfo(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "UserInfo{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }
}
```
从程序输出可以看到，atomicReference的持有的引用被修改了，但是原引用对象并没有发生改变。


###4.2 AtomicStampedReference
　　AtomicStampedReference，利用版本戳的形式记录了每次改变以后的版本号，这样的话就不会存在ABA问题了。 AtomicStampedReference有一个内部类Pair，使用Pair的int stamp作为计数器使用，看下Pair的源码：

```
 private static class Pair<T> {
        final T reference;
        final int stamp;
        private Pair(T reference, int stamp) {
            this.reference = reference;
            this.stamp = stamp;
        }
        static <T> Pair<T> of(T reference, int stamp) {
            return new Pair<T>(reference, stamp);
        }
    }
```
还是老王那个例子，如果使用AtomicStampedReference的话，老王更关心的是介绍的女朋友离过几次婚。用一段代码来模拟给老王介绍女朋友的场景：

```
import java.util.concurrent.atomic.AtomicStampedReference;

public class AtomicStampedReferenceDemo {
    static AtomicStampedReference<String> asr = new AtomicStampedReference("介绍的女朋友", 0);

    public static void main(String[] args) throws InterruptedException {
        final String oldReference = asr.getReference();//初始值，表示介绍的女朋友
        final int oldStamp = asr.getStamp();//初始版本0，表示介绍的女朋友没有离过婚

        Thread thread1 = new Thread(() -> {
            String newReference = oldReference + " 离婚1次";
            boolean first = asr.compareAndSet(oldReference, newReference,
                    oldStamp, oldStamp + 1);
            if (first) {
                System.out.println("介绍的女朋友第一次离婚。。。");
            }

            boolean second = asr.compareAndSet(newReference, oldReference + "又离婚了",
                    oldStamp + 1, oldStamp + 2);
            if (second) {
                System.out.println("介绍的女朋友第二次离婚。。。");
            }
        }, "介绍的女朋友离婚");

        Thread thread2 = new Thread(() -> {
            String reference = asr.getReference();//介绍的女朋友最新状态

            //判断介绍的女朋友最新状态是否符合老王的要求
            boolean flag = asr.compareAndSet(reference, reference + "没有离过婚",
                    oldStamp, oldStamp + 1);
            if (flag) {
                System.out.println("老王笑嘻嘻地对我说，介绍的女朋友符合我的要求");
            } else {
                System.out.println("老王拳头紧握地对我说，介绍的女朋友居然离过" + asr.getStamp() + "次婚,不符合我要求！！！！");
            }
        }, "老王相亲");
        thread1.start();
        thread1.join();
        thread2.start();
        thread2.join();
    }
}
```
 启动2个子线程，分别代表介绍的女朋友多次离婚以及老王相亲的场景。从程序输出可以看到，介绍的女朋友不符合老王的要求，老王为了避免喜当爹，果断拒绝了。

 老王判断的依据是，介绍的女朋友应该是没有离过婚，stamp值等于0才对。但是老王仔细一看，stamp已经是2，不符合我的要求，不能要。

###4.3 AtomicMarkableReference
　　AtomicMarkableReference，可以原子更新一个布尔类型的标记位和引用类型。构造方法是AtomicMarkableReference（V initialRef，booleaninitialMark）。AtomicMarkableReference也有一个内部类Pair，使用Pair的boolean mark来标记状态。还是老王那个例子，使用AtomicStampedReference可能关心的是离婚次数，AtomicMarkableReference关心的是有没有离过婚。用一段代码来模拟：

```
import java.util.concurrent.atomic.AtomicMarkableReference;

public class AtomicMarkableReferenceDemo {
    static AtomicMarkableReference markableReference;

    public static void main(String[] args) throws InterruptedException {
        String girl = "介绍的女朋友";
        markableReference = new AtomicMarkableReference(girl, false);
        Thread t1 = new Thread(() -> {
            markableReference.compareAndSet(girl, girl + "离婚", false, true);
            System.out.println(markableReference.getReference());
        }, "介绍的女朋友离婚了");

        Thread t2 = new Thread(() -> {
            //老王检查标记，只关心这个标志位
            boolean marked = markableReference.isMarked();
            if (marked) {
                System.out.println("你给我介绍的女朋友离过婚，我不要！！");
            } else {
                System.out.println("兄弟，大兄弟，亲生兄弟啊！！这个女朋友我要了");
            }
        }, "老王鉴定介绍的女朋友有没有离过婚");

        t1.start();
        t1.join();

        t2.start();
        t2.join();
    }
}
```
程序输出可以看到，老王还是坚持了自己的原则。



###4.4 AtomicIntegerArray
　　AtomicIntegerArray，元素可以原子更新的数组。其常用方法如下：

（1）int addAndGet（int i，int delta）：以原子方式将输入值与数组中索引i的元素相加。

（2）boolean compareAndSet（int i，int expect，int update）：如果当前值等于预期值，则以原子方式将数组位置i的元素设置成update值。

需要注意的是，数组value通过构造方法传递进去，然后AtomicIntegerArray会将当前数组复制一份，所以当AtomicIntegerArray对内部的数组元素进行修改时，不会影响传入的数组。

用法比较简单，看一个例子：

```
public class AtomicIntegerArrayDemo {
    static int[] value = new int[]{1, 2};//原始数组
    static AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(value);

    public static void main(String[] args) {
        atomicIntegerArray.getAndSet(0, 3);
        System.out.println("atomicIntegerArray的第一个元素：" + atomicIntegerArray.get(0));
        System.out.println("原始数组的第一个元素：" + value[0]);//原数组不会变化
    }
}
```
程序输出可以看到，原始数组并没有受到影响。

顺便看一下AtomicIntegerArray的构造方法：

```
 public AtomicIntegerArray(int[] array) {
        // Visibility guaranteed by final field guarantees
        this.array = array.clone();
    }
```
