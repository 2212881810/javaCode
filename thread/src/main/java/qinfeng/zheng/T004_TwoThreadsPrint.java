package qinfeng.zheng;

/**
 * @Author ZhengQinfeng
 * @Date 2021/2/24 20:48
 * @dec  synchronized 版本，自我感觉还可以
 */
public class T004_TwoThreadsPrint {

    static volatile boolean isNum = true;

    public static void main(String[] args) throws InterruptedException {
        Object lock = new Object();
        new Thread(() -> {
            synchronized (lock) {
                for (int i = 1; i <= 26; ) {
                    if (isNum) {
                        System.out.print(i);
                        i++;
                        isNum = false;
                        lock.notify();
                    } else {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();


        new Thread(() -> {
            synchronized (lock) {
                for (int i = 65; i <= 90; ) {
                    if (isNum) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println((char) i);
                        i++;
                        isNum = true;
                        lock.notify();
                    }
                }
            }
        }).start();
    }
}
