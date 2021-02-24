package qinfeng.zheng;

import java.util.PriorityQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * @Author ZhengQinfeng
 * @Date 2021/2/24 22:31
 * @dec
 */
public class T001_PriorityQueue {
    public static void main(String[] args) throws InterruptedException {
        PriorityQueue<Object> queue = new PriorityQueue<>();

//        queue.add(4);
//        queue.add(1);
//        queue.add(89);
//        queue.add(13);
//        queue.add(26);
//        queue.add(18);
//        queue.add(8);

        PriorityBlockingQueue<Object> blockingQueue = new PriorityBlockingQueue<>();
//        blockingQueue.add(1);

//        System.out.println(blockingQueue.poll());

//        System.out.println(blockingQueue.take());

        DelayQueue delayQueue = new DelayQueue();
    }
}
