package qinfen.zheng;

import java.io.IOException;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/27 0:05
 * @dec
 */
public class Test {
    public static void main(String[] args) throws InterruptedException, IOException {
        LinkedTransferQueue<String> queue = new LinkedTransferQueue<>();
        new Thread(()->{
            // not remove
            System.out.println(queue.peek());
        }).start();

        new Thread(()->{
            //remove element
            System.out.println(queue.poll());
        }).start();
        new Thread(()->{
            try {
                System.out.println("-----------");
                // blocking until element available
                System.out.println(queue.take());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }).start();

//        new Thread(()->{
//            try {
//                System.out.println("+++++++++");
//                // 直接将元素A交给consumer , 不会添加到队列的尾部
//                queue.transfer("A");
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//        }).start();


        TimeUnit.SECONDS.sleep(1);

        // take 阻塞的consumer
        System.out.println(queue.getWaitingConsumerCount());
        System.in.read();
    }
}
