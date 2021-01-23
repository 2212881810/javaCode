package com.qinfeng.zheng;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/23 9:58
 * @dec 广播员
 */
public final class Broadcast {
    /**
     * 信息组，实现群发功能[广播]
     * <p>
     * 这他妈还必须加static,因为GameMsgHandler不是1个单例呀，，每有1个client连接进来时都会创建1套pipeline管道
     */
    private Broadcast() {

    }

    private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 添加信道
     *
     * @param channel ： 信道
     */
    public static void addChannel(Channel channel) {
        if (channel != null) {
            channelGroup.add(channel);
        }

    }

    /**
     * 移除信道
     *
     * @param ch ： 信道
     */
    public static void removeChannel(Channel ch) {
        if (ch != null) {
            channelGroup.remove(ch);
        }

    }

    /**
     * 广播消息
     *
     * @param msg ： 消息内容
     */
    public static void broadcast(Object msg) {
        if (msg != null) {
            channelGroup.writeAndFlush(msg);
        }

    }

}
