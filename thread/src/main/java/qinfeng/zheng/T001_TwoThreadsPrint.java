package qinfeng.zheng;

import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;

/**
 * @Author ZhengQinfeng
 * @Date 2021/2/24 20:48
 * @dec  lockSupport试水版
 */
public class T001_TwoThreadsPrint {
    static Thread t1 = null, t2 = null;

    public static void main(String[] args) throws InterruptedException {

        t1 = new Thread(() -> {
            for (int i = 1; i <= 26; i++) {
                System.out.print(i);
                LockSupport.unpark(t2);
                if (i != 26) {
                    LockSupport.park();
                }
            }
        });

        t2 = new Thread(() -> {
            for (int i = 65; i <= 91; i++) {
                if (i != 91) {
                    LockSupport.park();
                }

                if (i == 91) {
                    return;
                }

                System.out.println((char) i);
                LockSupport.unpark(t1);
            }
        });

        t2.start();
        t1.start();

        System.out.println("main over~");
//
//        LockSupport.unpark(t1);
//        LockSupport.unpark(t2);

//        System.out.println((char) 90);
//        System.out.println((char) 91);
//        System.out.println((char) 92);
//        System.out.println((int) '[');

    }
}
