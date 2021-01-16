package qinfeng.zheng.other;

import java.util.concurrent.TimeUnit;

/**
 * @Author ZhengQinfeng
 * @Date 2020/12/27 14:56
 * @dec this 逃逸， 原因是new 一个对象 要分成3步。
 * 第2步和第3步可能产生指令重排序，
 * 所以，一般不要在构造方法启动线程，但是可以new线程
 */
public class Code04_ThisEscape {
    private Integer num = 8;

    public Code04_ThisEscape() {
        new Thread(() -> System.out.println(this.num)).start();  // this逃逸 ， 打印出来的num可能为0
    }

    public static void main(String[] args) throws InterruptedException {

        new Code04_ThisEscape();
        TimeUnit.SECONDS.sleep(1);
    }
}
