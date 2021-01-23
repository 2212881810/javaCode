package com.qinfeng.zheng.cmd.handler;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/23 10:22
 * @dec  命令处理器接口
 */
public interface ICmdHandler<TCmd extends GeneratedMessageV3> {

    /**
     * 处理命令
     * @param  ctx
     * @param tCmd
     */
    void handle(ChannelHandlerContext ctx,TCmd tCmd);
}
