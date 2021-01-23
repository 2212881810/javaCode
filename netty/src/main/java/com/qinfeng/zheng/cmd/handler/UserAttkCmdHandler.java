package com.qinfeng.zheng.cmd.handler;

import com.qinfeng.zheng.Broadcast;
import com.qinfeng.zheng.model.User;
import com.qinfeng.zheng.model.UserManager;
import com.qinfeng.zheng.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/23 15:20
 * @dec
 */
public class UserAttkCmdHandler implements ICmdHandler<GameMsgProtocol.UserAttkCmd> {
    private static final Logger logger = LoggerFactory.getLogger(UserAttkCmdHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserAttkCmd cmd) {
        if (ctx == null || cmd == null) {
            return;
        }


        // 攻击用户id
        Integer attkUserId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (attkUserId == null) {

            return;
        }

        // 目标用户id
        int targetUserId = cmd.getTargetUserId();
        User targetUser = UserManager.getUserByUserId(targetUserId);
        if (targetUser == null) {
            broadcastAttkResult(attkUserId, -1);
            return;
        }
        logger.info("当前线程：{}" , Thread.currentThread().getName());
        final int dmgPoint = 10;
        targetUser.curHp = targetUser.curHp - dmgPoint;

        // 广播攻击结果
        broadcastAttkResult(attkUserId, targetUserId);
        // 广播减箅结果
        broadcastSubtractHpResult(targetUserId, dmgPoint);


        if (targetUser.curHp <= 0) {
            // 广播死亡结果
            broadcastDieResult(targetUserId);
        }


    }

    /**
     * 广播攻击结果
     *
     * @param attUserId
     * @param targetUserId
     */
    private static void broadcastAttkResult(int attUserId, int targetUserId) {
        if (attUserId <= 0) {
            return;
        }

        GameMsgProtocol.UserAttkResult.Builder builder = GameMsgProtocol.UserAttkResult.newBuilder();
        builder.setAttkUserId(attUserId);
        builder.setTargetUserId(targetUserId);
        GameMsgProtocol.UserAttkResult userAttkResult = builder.build();
        Broadcast.broadcast(userAttkResult);
    }

    /**
     * 广播减血结果
     *
     * @param targetUserId
     * @param subtractHp
     */
    private static void broadcastSubtractHpResult(int targetUserId, int subtractHp) {
        if (targetUserId <= 0 || subtractHp <= 0) {
            return;
        }

        GameMsgProtocol.UserSubtractHpResult.Builder resultBuilder = GameMsgProtocol.UserSubtractHpResult.newBuilder();
        resultBuilder.setTargetUserId(targetUserId);
        resultBuilder.setSubtractHp(subtractHp);
        GameMsgProtocol.UserSubtractHpResult subtractHpResult = resultBuilder.build();


        Broadcast.broadcast(subtractHpResult);
    }

    /**
     * 广播死亡结果
     *
     * @param targetUserId
     */
    private static void broadcastDieResult(int targetUserId) {
        if (targetUserId <= 0) {
            return;
        }
        GameMsgProtocol.UserDieResult.Builder builder = GameMsgProtocol.UserDieResult.newBuilder();
        builder.setTargetUserId(targetUserId);
        GameMsgProtocol.UserDieResult userDieResult = builder.build();
        Broadcast.broadcast(userDieResult);
    }
}
