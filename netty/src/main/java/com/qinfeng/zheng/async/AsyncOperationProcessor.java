package com.qinfeng.zheng.async;

import com.qinfeng.zheng.MainMsgProcessor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/23 21:10
 * @dec 异步操作器
 */
public class AsyncOperationProcessor {


    private static final AsyncOperationProcessor instance = new AsyncOperationProcessor();

    public static AsyncOperationProcessor getInstance() {
        return instance;
    }

    /**
     * 创建1个单线程的线程池
     */
/*    private static final ExecutorService es = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable);
        thread.setName("AsyncOperationProcessor");  // 线程命名
        return thread;
    });*/


    /**
     * 创建单线程数组
     */
    private final ExecutorService[] esArr = new ExecutorService[8];


    private AsyncOperationProcessor() {
        for (int i = 0; i < esArr.length; i++) {
            final String threadName = "AsyncOperationProcessor-" + i;
            esArr[i] = Executors.newSingleThreadExecutor(r -> {

                Thread newThread = new Thread(r);
                newThread.setName(threadName);
                return newThread;

            });
        }

    }

    /**
     * 执行异步操作
     *
     * @param op
     */
    public void process(IAsyncOperation op) {
        if (op == null) {
            return;
        }

        // 根据bindId分配1个线程池去执行异步操作过程
        int bindId = Math.abs(op.getBindId());
        int esIndex = bindId % esArr.length;
        ExecutorService es = esArr[esIndex];

        es.submit(() -> {

            // 执行异步操作
            op.doAsync();  // doAsync这个操作是在Async线程中执行的


            // 回来主线程执行完成逻辑
//            MainMsgProcessor.getInstance().process(()->op.doFinish());


            // 优化代码
            // doFinish这个操作是Main线程中执行的
            MainMsgProcessor.getInstance().process(op::doFinish);

        });

//        es.submit(op::doAsync); // 可以使用这种方式简写runnable


    }
}
