package qinfeng.zheng.reactor;

import java.net.InetSocketAddress;
import java.nio.channels.Channel;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/16 21:34
 * @dec 该类一new出来就是boss group,用于接收client，
 * 调用setWorker方法之后，这个boss group 就维护了一个worker group的引用
 */
public class WorkThreadGroup {
    // 每个组中的线程数组
    WorkerThread[] wts;

    ServerSocketChannel server = null;

    // 这句代码还是很有意思的~~~
    WorkThreadGroup workers = this;

    AtomicInteger threadIndex = new AtomicInteger(0);


    WorkThreadGroup(int threads) {
        wts = new WorkerThread[threads];
        for (int i = 0; i < threads; i++) {
            wts[i] = new WorkerThread(this);  // 这种this传参真的有点蛋疼~ 平时基本没有使用
            new Thread(wts[i]).start();// 启动线程
        }

    }


    public void bind(int port) {
        try {
            // 因为这里，所以WorkThreadGroup 一new出来就是boss线程组了
            server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(port));

            // 将server 注册到1个selector上去 ， 这里肯定是注册到boss group 中的某1个线程的selector上的，
            registerSelector(server);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 调用该方法之后，boss线程组就维护了一个worker线程的引用！~
     *
     * @param workers ： 工作线程，处理io事件的
     */
    public void setWorker(WorkThreadGroup workers) {
        this.workers = workers;

    }


    /**
     * 将Channel注册到某个selector上去
     *
     * 分为ServerSocketChannel和SocketChannel
     *  其中ServerSocketChannel注册到boss group 线程
     *  SocketChannel注册到 worker group 线程
     *
     * @param channel ： ServerSocketChannel或者是SocketChannel
     */
    public void registerSelector(Channel channel) {
        try {
            // 如果是Server 端 ， 就从boss 线程组中挑选出1个线程(线程绑定了selector)进行注册
            if (channel instanceof ServerSocketChannel) {
                WorkerThread wt = nextBossThread(); // 从 boss group 中选1个线程

                // 通过queue进行数据交互
                wt.queue.put(channel);

                // 给boss中的线程绑定worker线程组
                wt.setWorker(workers);
                wt.selector.wakeup(); // 唤醒wt线程中的selector, 之前它一直被select方法阻塞着的

            } else {
                WorkerThread workerThread = nextWorkerThread();
                // 通过queue 传递消息，workerThread线程内部从queue中获取到channel，然后注册到相应的selector上
                workerThread.queue.add(channel);
                // 唤醒workerThread中的selector，让它继承干活~
                workerThread.selector.wakeup();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 从boss 线程组的workers线程组中选择线程
     *
     * @return
     */
    private WorkerThread nextWorkerThread() {
        int index = threadIndex.getAndIncrement() % this.workers.wts.length;
        return this.workers.wts[index];
    }

    /**
     * boss group中选线程
     *
     * @return
     */
    private WorkerThread nextBossThread() {
        // 轮询策略有个缺点，就是选择倾斜
        int index = threadIndex.getAndIncrement() % this.wts.length;
        return this.wts[index];
    }


}
