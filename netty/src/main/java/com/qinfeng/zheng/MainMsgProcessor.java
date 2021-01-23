package com.qinfeng.zheng;

import com.google.protobuf.GeneratedMessageV3;
import com.qinfeng.zheng.async.IAsyncOperation;
import com.qinfeng.zheng.cmd.handler.CmdHandlerFactory;
import com.qinfeng.zheng.cmd.handler.ICmdHandler;
import com.qinfeng.zheng.handler.GameMsgHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/23 18:10
 * @dec 主消息处理器
 */
public final class MainMsgProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainMsgProcessor.class);

    private MainMsgProcessor() {

    }

    private static final MainMsgProcessor instance = new MainMsgProcessor();

    /**
     * 获取单例对象
     *
     * @return 单例对象
     */
    public static MainMsgProcessor getInstance() {
        return instance;
    }

    /**
     * 创建1个单线程的线程池
     */
    private static final ExecutorService es = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable);
        thread.setName("MainMsgProcessor");  // 线程命名
        return thread;
    });


    public void process(ChannelHandlerContext ctx, Object msg) {
        LOGGER.info("receive client msgClass : {} , msg :{}  ", msg.getClass().getSimpleName(), msg);

        // 通过这个方法，保证client提交的消息都会交给MainMsgProcessor这个线程去处理
        // 原本消息是在NioEvenetLoop池中线程去执行，并行执行的，可能产生并发问题
        es.submit(() -> {
            try {
                // 设计模式第1个原则，开闭原则！！！！
                ICmdHandler<? extends GeneratedMessageV3> cmdHandler = CmdHandlerFactory.create(msg.getClass());

                if (null != cmdHandler) {
                    cmdHandler.handle(ctx, cast(msg));
                }

            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        });
    }

    /**
     * 转型为命令对象
     *
     * @param msg
     * @param <TCmd>
     * @return
     */
    private static <TCmd extends GeneratedMessageV3> TCmd cast(Object msg) {
        if (msg == null) {
            return null;
        } else {
            return (TCmd) msg;
        }


    }

    public void process(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        es.submit(runnable);

    }
}
