package com.qinfeng.zheng.handler;

import com.google.protobuf.GeneratedMessageV3;
import com.qinfeng.zheng.GameMsgRecognizer;
import com.qinfeng.zheng.msg.GameMsgProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/22 23:36
 * @dec 自定义游戏消息的编码器, 从服务端发到客户端 ChannelOutboundHandlerAdapter
 */
public class GameMsgEncoder extends ChannelOutboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(GameMsgEncoder.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (ctx == null || msg == null) {
            return;
        }


        try {

            if (!(msg instanceof GeneratedMessageV3)) {

                super.write(ctx, msg, promise);
                return;
            }

         /*
          FIXME： 重构：
         int msgCode = -1;
         if (msg instanceof GameMsgProtocol.UserEntryResult) {
                msgCode = GameMsgProtocol.MsgCode.USER_ENTRY_RESULT_VALUE;
            } else if (msg instanceof GameMsgProtocol.WhoElseIsHereResult) {
                msgCode = GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_RESULT_VALUE;
            } else if (msg instanceof GameMsgProtocol.UserMoveToResult) {
                msgCode = GameMsgProtocol.MsgCode.USER_MOVE_TO_RESULT_VALUE;
            } else if (msg instanceof GameMsgProtocol.UserQuitResult) {
                msgCode = GameMsgProtocol.MsgCode.USER_QUIT_RESULT_VALUE;
            } else {
                logger.error("无法识别消息类型,msgClass = {} ", msg.getClass().getSimpleName());
                super.write(ctx, msg, promise);
                return;
            }*/

            // 重构之后的代码
            int msgCode = GameMsgRecognizer.getMsgCodeByMsgClass(msg.getClass());

            if (msgCode == -1) {
                logger.error("无法识别消息类型,msgClass = {} ", msg.getClass().getSimpleName());
                super.write(ctx, msg, promise);
                return;
            }

            // 消息体
            byte[] msgBody = ((GeneratedMessageV3) msg).toByteArray();
            ByteBuf byteBuf = ctx.alloc().buffer();
            byteBuf.writeShort((short) msgBody.length); // 消息的长度
            byteBuf.writeShort((short) msgCode); // 消息编号
            byteBuf.writeBytes(msgBody); // 消息体

            BinaryWebSocketFrame outFrame = new BinaryWebSocketFrame(byteBuf);
            super.write(ctx, outFrame, promise);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
