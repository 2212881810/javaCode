package com.qinfeng.zheng.cmd.handler;

import com.qinfeng.zheng.Broadcast;
import com.qinfeng.zheng.model.User;
import com.qinfeng.zheng.model.UserManager;
import com.qinfeng.zheng.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/23 10:14
 * @dec
 */
public class UserEntryCmdHandler implements ICmdHandler<GameMsgProtocol.UserEntryCmd> {

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserEntryCmd cmd) {
        if (ctx == null || cmd == null) {
            return;
        }

        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (userId == null) {
            return;
        }
        User existUser = UserManager.getUserByUserId(userId);




        // 构造1个UserEntryResult对象
        GameMsgProtocol.UserEntryResult.Builder builder = GameMsgProtocol.UserEntryResult.newBuilder();
        builder.setUserId(userId);
        builder.setUserName(existUser.userName);
        builder.setHeroAvatar(existUser.getHeroAvatar());
        GameMsgProtocol.UserEntryResult result = builder.build();

        // 把UserEntryResult通过channel全局广播
        Broadcast.broadcast(result);  // 群发

    }
}
