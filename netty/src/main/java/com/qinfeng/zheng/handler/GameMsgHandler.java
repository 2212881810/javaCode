package com.qinfeng.zheng.handler;

import com.qinfeng.zheng.User;
import com.qinfeng.zheng.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/19 23:25
 * @dec
 */
public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameMsgHandler.class);

    /**
     * 信息组，实现群发功能[广播]
     * <p>
     * 这他妈还必须加static,因为GameMsgHandler不是1个单例呀，，每有1个client连接进来时都会创建1套pipeline管道
     */
    private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static final Map<Integer, User> userMap = new HashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (ctx == null) {
            return;
        }
        try {
            channelGroup.add(ctx.channel());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOGGER.info("receive msg : {} ", msg);

        // 因为GameMsgDecoder解码器将前端发送过来的消息进行了解码，所以消息到达这个处理器时类型已经转换成了 GameMsgProtocol
        if (msg instanceof GameMsgProtocol.UserEntryCmd) {
            GameMsgProtocol.UserEntryCmd cmd = (GameMsgProtocol.UserEntryCmd) msg;
            int userId = cmd.getUserId();
            String heroAvatar = cmd.getHeroAvatar();

            User user = new User();
            user.setUserId(userId);
            user.setHeroAvatar(heroAvatar);

            userMap.put(userId, user);
            // 构造1个UserEntryResult对象
            GameMsgProtocol.UserEntryResult.Builder builder = GameMsgProtocol.UserEntryResult.newBuilder();
            builder.setUserId(userId);
            builder.setHeroAvatar(heroAvatar);
            GameMsgProtocol.UserEntryResult result = builder.build();

            // 把UserEntryResult通过channel全局广播
            channelGroup.writeAndFlush(result);  // 群发

        } else if (msg instanceof GameMsgProtocol.WhoElseIsHereCmd) { // 用户登录之后，会发1个还有谁的命令
            GameMsgProtocol.WhoElseIsHereResult.Builder resultBuilder = GameMsgProtocol.WhoElseIsHereResult.newBuilder();
            for (User user : userMap.values()) {
                if (user == null) {
                    continue;
                }
                GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder userInfoBuilder = GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();
                userInfoBuilder.setUserId(user.getUserId());
                userInfoBuilder.setHeroAvatar(user.getHeroAvatar());
                resultBuilder.addUserInfo(userInfoBuilder);
            }


            GameMsgProtocol.WhoElseIsHereResult newResult = resultBuilder.build();
            ctx.writeAndFlush(newResult); // 给当前登录用户
        }
    }
}
