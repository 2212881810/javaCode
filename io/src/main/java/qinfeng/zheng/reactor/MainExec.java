package qinfeng.zheng.reactor;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/16 21:32
 * @dec
 */
public class MainExec {
    public static void main(String[] args) {
        // 创建boss group ,意味着内部创建了两个线程，open了两个selector,
        // 而且这个两个线程一创建新运行了，不过会阻塞在select方法处，等待wakeup方法将其唤醒之后，再执行后面的逻辑
        WorkThreadGroup bossGroup = new WorkThreadGroup(2);

        // 创建这个worker group ,意味着内部创建了3个线程 ，open了3个selector
        WorkThreadGroup workerGroup = new WorkThreadGroup(3);


        bossGroup.setWorker(workerGroup);

        // open了个ServerSocketChannel，同时这个4个channel是交给boss group中的两个线程去执行中【run方法】,注册selector
        bossGroup.bind(9999);
        bossGroup.bind(8888);
        bossGroup.bind(7777);
        bossGroup.bind(6666);

    }
}
