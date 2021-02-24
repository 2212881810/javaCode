package qinfeng.zheng;

import org.omg.PortableInterceptor.INACTIVE;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @Author ZhengQinfeng
 * @Date 2021/2/24 23:09
 * @dec
 */
public class T002_DelayQueue {
    public static void main(String[] args) {
        DelayQueue<Delayed> delayQueue = new DelayQueue<>();
        delayQueue.add(new Item());
        delayQueue.poll();
    }


    private static class Item implements Delayed {


        @Override
        public long getDelay(TimeUnit unit) {
            return 0;
        }

        @Override
        public int compareTo(Delayed o) {
            return 0;
        }
    }
}



