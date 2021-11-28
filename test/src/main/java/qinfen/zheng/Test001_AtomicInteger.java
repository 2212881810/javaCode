package qinfen.zheng;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

/**
 * @Author ZhengQinfeng
 * @Date 2021/3/21 19:05
 * @dec
 */
public class Test001_AtomicInteger {
    public static void main(String[] args) {
        AtomicInteger count = new AtomicInteger(0);


        LongAdder longAdder = new LongAdder();


        longAdder.add(1);
    }
}
