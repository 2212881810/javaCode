package qinfeng.zheng;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @Author ZhengQinfeng
 * @Date 2021/4/16 22:32
 * @dec
 */
public class T005_CyclicBarrierTest {
    public static void main(String[] args) {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(3,()->{
            System.out.println("线程到齐了...");
        });

        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    cyclicBarrier.await();
                    System.out.println(Thread.currentThread().getName()+" running");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }

            }).start();
        }

        System.out.println("main thread over");
    }
}
