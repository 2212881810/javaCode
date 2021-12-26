package qinfeng.zheng;

/**
 * @Author ZhengQinfeng
 * @Date 2021/12/4 21:10
 * @dec  javap -c ThreadActiveCount 查看字节码指令
 */
public class ThreadActiveCount {
    private static final int THREADS_COUNT = 20;
    public static volatile int race = 0;

    public static void increase() {
        race++;
    }

    public static void main(String[] args) {
        Thread[] threads = new Thread[THREADS_COUNT];
        for (int i = 0; i < THREADS_COUNT; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 10000; i++) {
                        increase();
                    }
                }
            });
            System.out.println(Thread.activeCount());
            threads[i].start();
        }

        while (Thread.activeCount() > 2) {
            Thread.yield();
        }

        System.out.println(race);
    }
}
