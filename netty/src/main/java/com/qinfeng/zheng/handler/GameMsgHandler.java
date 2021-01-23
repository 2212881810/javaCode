package com.qinfeng.zheng.handler;

import com.qinfeng.zheng.Broadcast;
import com.qinfeng.zheng.MainMsgProcessor;
import com.qinfeng.zheng.model.UserManager;
import com.qinfeng.zheng.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/19 23:25
 * @dec 非单例
 */
public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameMsgHandler.class);


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (ctx == null) {
            return;
        }
        try {
            Broadcast.addChannel(ctx.channel());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * 浏览器关闭，人物退场，
     * <p>
     * 离线
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        try {


            Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();


            super.handlerRemoved(ctx);
            Broadcast.removeChannel(ctx.channel());


            if (userId == null) {
                return;
            }

            UserManager.removeByUserId(userId);

            GameMsgProtocol.UserQuitResult.Builder newBuilder = GameMsgProtocol.UserQuitResult.newBuilder();
            newBuilder.setQuitUserId(userId);
            GameMsgProtocol.UserQuitResult userQuitResult = newBuilder.build();
            Broadcast.broadcast(userQuitResult); // 广播


        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        MainMsgProcessor.getInstance().process(ctx, msg);

    }


}
