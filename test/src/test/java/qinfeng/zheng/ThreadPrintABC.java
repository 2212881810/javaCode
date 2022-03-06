package qinfeng.zheng;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author ZhengQinfeng
 * @Date 2022/2/27 11:24
 * @dec
 */
public class ThreadPrintABC {


    public static void main(String[] args) throws InterruptedException {

        ReentrantLock lock = new ReentrantLock();
        Condition cA = lock.newCondition();
        Condition cB = lock.newCondition();
        Condition cC = lock.newCondition();

        AtomicInteger count = new AtomicInteger(0);

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                lock.lock();
                while (count.get() % 3 != 0) {
                    try {
                        cA.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("A");
                count.incrementAndGet();
                cB.signal();
                lock.unlock();
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                lock.lock();
                while (count.get() % 3 != 1) {
                    try {
                        cB.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("B");
                count.incrementAndGet();
                cC.signal();
                lock.unlock();
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                lock.lock();
                while (count.get() % 3 != 2) {
                    try {
                        cC.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("C");
                count.incrementAndGet();
                cA.signal();
                lock.unlock();
            }
        }).start();


        Thread.sleep(Integer.MAX_VALUE);
    }
}
