import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @Author ZhengQinfeng
 * @Date 2021/2/5 21:53
 * @dec
 */
public class Test {
    public static void main(String[] args) throws InterruptedException {

        Config config = new Config();
        config.useSingleServer().setAddress("redis://120.79.41.56:6379").setPassword("123456");
        RedissonClient redissonClient = Redisson.create(config);

        new Thread(()->{
            boolean b = false;
            try {
                RLock myLock = redissonClient.getLock("myLock");
                b = myLock.tryLock(100, TimeUnit.HOURS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(b);
        }).start();


        new Thread(()->{
            boolean b = false;
            try {
                RLock myLock = redissonClient.getLock("myLock");
                b = myLock.tryLock(100, TimeUnit.HOURS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(b);
        }).start();

//
//        for (int i = 0; i < 100; i++) {
//            long l = ThreadLocalRandom.current().nextLong(10 / 2, 10);
//            System.out.println(l);
//        }
    }

    final List<RLock> locks = new ArrayList<RLock>();

    public void lockInterruptibly(long leaseTime, TimeUnit unit) throws InterruptedException {
        // 基准等待时间
        long baseWaitTime = locks.size() * 1500;

        long waitTime = -1;

        if (leaseTime == -1) {
            waitTime = baseWaitTime;
            unit = TimeUnit.MILLISECONDS;
        } else {
            waitTime = unit.toMillis(leaseTime);
            if (waitTime <= 2000) {
                waitTime = 2000;
            } else if (waitTime <= baseWaitTime) {
                waitTime = ThreadLocalRandom.current().nextLong(waitTime/2, waitTime);
            } else {
                waitTime = ThreadLocalRandom.current().nextLong(baseWaitTime, waitTime);
            }
            waitTime = unit.convert(waitTime, TimeUnit.MILLISECONDS);
        }

        while (true) {
//            if (tryLock(waitTime, leaseTime, unit)) {
//                return;
//            }
        }
    }
}
