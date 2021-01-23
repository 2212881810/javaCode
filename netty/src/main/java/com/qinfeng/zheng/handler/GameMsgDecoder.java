package com.qinfeng.zheng.handler;

import com.google.protobuf.Message;
import com.qinfeng.zheng.GameMsgRecognizer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/22 22:47
 * @dec 自定义消息解码器
 */
public class GameMsgDecoder extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(GameMsgDecoder.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (ctx == null || msg == null) {
            return;
        }
        if (!(msg instanceof BinaryWebSocketFrame)) {
            return;
        }
        try {
            // 前端发送到服务端的消息数据类型是BinaryWebSocketFrame
            BinaryWebSocketFrame inputFrame = (BinaryWebSocketFrame) msg;
            ByteBuf byteBuf = inputFrame.content();
            // 因为websocket默认的处理器已经把粘包的问题处理掉了【HttpServerCodec，HttpObjectAggregator，WebSocketServerProtocolHandler】， 所以我们不需要这个长度数据 ，
            // 如果是使用socket，需要使用这个长度自己去处理消息体
            // 消息构成：2位消息长度 + 2位消息号 + 消息体长度
            short msgLen = byteBuf.readShort();  // short占两个字节， 读取消息的长度
            short msgCode = byteBuf.readShort();// 读取消息编号

            byte[] msgBody = new byte[byteBuf.readableBytes()]; // 拿到消息体
            byteBuf.readBytes(msgBody);  // 把余下了内容读到msgBody中




    /*
    重构
     GeneratedMessageV3 cmd = null;
     switch (msgCode) {
                case GameMsgProtocol.MsgCode.USER_ENTRY_CMD_VALUE:
                    cmd = GameMsgProtocol.UserEntryCmd.parseFrom(msgBody);
                    break;

                case GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_CMD_VALUE:
                    cmd = GameMsgProtocol.WhoElseIsHereCmd.parseFrom(msgBody);
                    break;
                case GameMsgProtocol.MsgCode.USER_MOVE_TO_CMD_VALUE:
                    cmd = GameMsgProtocol.UserMoveToCmd.parseFrom(msgBody);
                    break;
                default:
                    break;

            }*/

            // 上面代码重构之后的代码
            // 获取消息构造器
            Message.Builder builder = GameMsgRecognizer.getBuilderByMsgCode(msgCode);
            if (builder == null) {
                return;
            }
            builder.clear();
            builder.mergeFrom(msgBody);

            // 构造消息体
            Message cmd = builder.build();


            if (cmd != null) {
                // 把原消息解码成GameMsgProtocol.XX对象，重新放入channel中，然后消息传递给pipeline管理链中的下1个处理器
                ctx.fireChannelRead(cmd);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
