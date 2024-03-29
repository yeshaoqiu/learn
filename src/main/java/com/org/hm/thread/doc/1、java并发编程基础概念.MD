[TOC]

##1、什么是进程和线程

###1.1 进程是程序运行资源分配的最小单位

　　进程是操作系统进行资源分配的最小单位，其中资源包括：CPU、内存空间、磁盘IO等，同一进程中的多个线程共享该进程中的全部系统资源，而进程和进程之间是相互独立的。进程是具有一定独立功能的程序关于某个数据集合上的一次运行活动，进程是系统进行资源分配和调度的一个独立单位。

　　进程是程序在计算机上的一次执行活动。当你运行一个程序，你就启动了一个进程。显然，程序是死的、静态的，进程是活的、动态的。进程可以分为系统进程和用户进程。凡是用于完成操作系统的各种功能的进程就是系统进程，它们就是处于运行状态下的操作系统本身，用户进程就是所有由你启动的进程。

###1.2 线程

　　线程是进程的一个实体，必须依赖一个进程而存在，是CPU调度和分派的基本单位，它是比进程更小的、能独立运行的基本单位。线程自己基本上不拥有系统资源，只拥有一点在运行中必不可少的资源(如程序计数器，一组寄存器和栈)，但是它和与同属一个进程的其他的线程共享进程所拥有的全部资源。

###1.3 线程无处不在

　　任何一个程序都必须要创建线程，特别是Java不管任何程序都必须启动一个main函数的主线程；Java Web开发里面的定时任务、定时器、JSP和 Servlet、异步消息处理机制,远程访问接口RM等；任何一个监听事件； onclick的触发事件等都离不开线程和并发的知识。

 

##2、CPU核心数和线程数的关系
###2.1 多核心　

　　指单芯片多处理器( Chip Multiprocessors，简称CMP)，CMP是由美国斯坦福大学提出的，其思想是将大规模并行处理器中的SMP(对称多处理器)集成到同一芯片内，各个处理器并行执行不同的进程。这种依靠多个CPU同时并行地运行程序是实现超高速计算的一个重要方向，称为并行处理。

###2.2 多线程

Simultaneous Multithreading，简称SMT。让同一个处理器上的多个线程同步执行并共享处理器的执行资源。

###2.3 核心数和线程数
　　目前主流CPU都是多核的。增加核心数目就是为了增加线程数，因为操作系统是通过线程来执行任务的，一般情况下它们是1:1对应关系，也就是说四核CPU一般拥有四个线程。但引入超线程技术后，使核心数与逻辑处理器（线程数）形成1：2的关系。


##3、CPU时间片轮转机制

　　我们平时在开发过程中，感觉并没有受CPU核心数的限制，想启动线程就启动线程，哪怕是在单核CPU上，为什么？这是因为操作系统提供了一种CPU时间片轮转机制。

时间片轮转调度是一种最古老、最简单、最公平且使用最广的算法，又称RR调度。每个进程被分配一个时间段，称作它的时间片，即该进程允许运行的时间。

百度百科对CPU时间片轮转机制原理解释如下:

　　如果在时间片结束时进程还在运行，则CPU将被剥夺并分配给另一个进程。如果进程在时间片结束前阻塞或结束，则CPU当即进行切换。调度程序所要做的就是维护一张就绪进程列表，当进程用完它的时间片后，它被移到队列的末尾。而这种时间片轮转是有代价的，往往还会伴随着上下文切换。

 

##4、上下文切换
　　任何对进程或者线程的调度，都会引入额外的开销，这个开销中就包括上下文切换（Context Switch），有时也称做进程切换或任务切换，是指CPU 从一个进程或线程切换到另一个进程或线程。在上下文切换过程中，CPU会停止处理当前运行的程序，并保存当前程序运行的具体位置以便之后继续运行。从这个角度来看，上下文切换有点像我们同时阅读几本书，在来回切换书本的同时我们需要记住每本书当前读到的页码。

　　上下文切换通常是计算密集型的。也就是说上下文切换对系统来说意味着消耗大量的 CPU 时间，事实上，可能是操作系统中时间消耗最大的操作。根据Tsuna的测试报告，每次上下文切换都需要几十纳秒到数微秒的CPU时间。这个时间还是相当可观的，特别是上下文切换次数较多的情况下，很容易导致CPU将大量的时间耗费在寄存器、内核栈、以及虚拟内存等资源的保存和恢复上，进而大大缩短了真正运行的时间。

引起线程上下文切换的原因大概有以下几种：

（1）当前执行任务的时间片用完之后，系统CPU正常调度下一个任务；

（2）当前执行任务碰到IO阻塞，调度器将此任务挂起，继续下一任务；

（3）多个任务抢占锁资源，当前任务没有抢到锁资源，被调度器挂起，继续下一任务；

（4）用户代码挂起当前任务，让出CPU时间。

 

##5、并行和并发
###5.1 并行
　　举个例子，某高速路单向3车道，那么此高速路单向最大并行车辆数就是3。此高速路并排行走的车辆小于等于3时，车辆可以并行运行。
 CPU也是这个原理，一个CPU相当于一条高速公路，核心数或者线程数就相当于并排可以通行的车。



###5.2 并发
　　当谈论并发的时候一定要加个单位时间，就是说单位时间内并发量是多少，并发离开了单位时间其实是没有意义的。

　　俗话说，一心不能二用，这对计算机也一样，原则上一个CPU只能分配给一个进程，以便运行这个进程。我们通常使用的计算机中只有一个CPU，也就是说只有一颗心，要让它一心多用同时运行多个进程，就必须使用并发技术。实现并发技术相当复杂，最容易理解的是“时间片轮转进程调度算法”。举个例子，还是刚刚这条高度路，某一段在进行道路养护，3车道突然变1车道，堵车那是顺理成章了，此时所有的车辆都要交替的走这个车道通过。

##6、并发编程的意义和好处
　　由于多核多线程的CPU的诞生，多线程、高并发的编程越来越受重视和关注。多线程可以给程序带来如下好处。

(1)充分利用CPU的资源

　　现在市面上没有CPU的内核不使用多线程并发机制的，特别是服务器还不止一个CPU，如果还是使用单线程的技术做思路，明显就out了。因为程序的基本调度单元是线程，并且一个线程也只能在一个CPU的一个核的一个线程跑，例如笔者的CPU是4核8线程的，如果用来跑一个线程的程序的话，那是要浪费7/8的CPU性能。如果设计一个多线程的程序的话，那它就可以同时在多个CPU的多个核的多个线程上跑，可以充分地利用CPU，减少CPU的空闲时间，发挥它的运算能力，提高并发量。

就像我们平时坐地铁一样，很多人坐长线地铁的时候都在认真看书，而不是为了坐地铁而坐地铁，到家了再去看书，这样你的时间就相当于有了两倍。这就是为什么有些人时间很充裕，而有些人老是说没时间的一个原因。工作也是这样，有的时候可以并发地去做几件事情，充分利用我们的时间，CPU也是一样，也要充分利用。

(2)加快响应用户的时间

　　比如我们经常用的迅雷下载，都喜欢多开几个线程去下载，谁都不愿意用一个线程去下载，为什么呢？答案很简单，就是多个线程下载快啊。

(3)可以使你的代码模块化、异步化、简单化

　　例如我们实现电商系统，下订单和给用户发送短信、邮件就可以进行拆分，将给用户发送短信、邮件这两个步骤独立为单独的模块，并交给其他线程去执行。这样既增加了异步的操作，提升了系统性能，又使程序模块化，清晰化和简单化。

 

##7、并发编程需要注意事项
(1)线程之间的安全性

　　从前面的介绍中我们知道，在同一个进程里面的多线程是资源共享的，也就是都可以访问同一个内存地址当中的一个变量。例如：若每个线程中对全局变量、静态变量只有读操作，而无写操作，一般来说这个全局变量是线程安全的。若有多个线程同时执行写操作，一般都需要考虑线程同步，否则就可能影响线程安全。

 (2)线程之间的死锁

　　Java可以用锁机制来解决线程之间的安全性问题，但是在使用锁的时候可能会产生Java线程死锁问题，因为不同的线程都在等待那些根本不可能被释放的锁，从而导致所有的工作都无法完成。假设有两个线程A和B，分别代表两个饥饿的人，他们必须共享刀叉并轮流吃饭。他们都需要获得两个锁：共享刀和共享叉的锁。

假如线程A获得了刀，而线程B获得了叉。线程A就会进入阻塞状态来等待获得叉，而线程B则阻塞来等待线程A所拥有的刀。这只是人为设计的例子，但尽管在运行时很难探测到死锁问题，这类情况却时常发生。

(3)线程太多了会将服务器资源耗尽形成死机宕机

　　线程数太多有可能造成系统创建大量线程而导致消耗完系统内存以及CPU的“过渡切换”，造成系统的死机，那么我们该如何解决这类问题呢?

某些系统资源是有限的，如文件描述符。多线程程序可能耗尽资源，因为每个线程都可能希望有一个这样的资源。如果线程数相当大，或者某个资源的侯选线程数远远超过了可用的资源数则最好使用资源池。一个最好的示例是数据库连接池。只要线程需要使用一个数据库连接，它就从池中取出一个，使用以后再将它返回池中。
