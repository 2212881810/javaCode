package qinfeng.zheng;

/**
 * @Author ZhengQinfeng
 * @Date 2021/2/24 20:48
 * @dec  synchronized 版本，看起来还可以
 */
public class T003_TwoThreadsPrint {


    public static void main(String[] args) {
        Object lock = new Object();
        new Thread(() -> {
            synchronized (lock) {
                for (int i = 1; i <= 26; i++) {
                    System.out.print(i);
                    try {
                        lock.notify();
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


//        Thread.currentThread().sleep(1000);
        new Thread(() -> {
            synchronized (lock) {
                for (int i = 65; i <= 90; i++) {
                    System.out.println((char) i);
                    lock.notify();
                    try {
                        if (i != 90) {
                            lock.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }
}
