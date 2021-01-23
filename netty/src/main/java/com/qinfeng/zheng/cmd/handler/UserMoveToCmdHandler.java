package com.qinfeng.zheng.cmd.handler;

import com.qinfeng.zheng.Broadcast;
import com.qinfeng.zheng.model.User;
import com.qinfeng.zheng.model.UserManager;
import com.qinfeng.zheng.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/23 10:18
 * @dec
 */
public class UserMoveToCmdHandler implements ICmdHandler<GameMsgProtocol.UserMoveToCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserMoveToCmd cmd) {

        if (ctx == null || cmd == null) {
            return;
        }


        // 用户移动
        // 从session中取出用户id
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (userId == null) {
            return;
        }

        User existUser = UserManager.getUserByUserId(userId);

        if (existUser == null) {
            return;
        }

        long nowTime = System.currentTimeMillis();
        existUser.moveState.startTime = nowTime;
        existUser.moveState.fromPosX = cmd.getMoveFromPosX();
        existUser.moveState.fromPosY = cmd.getMoveFromPosY();
        existUser.moveState.toPosX = cmd.getMoveToPosX();
        existUser.moveState.toPosY = cmd.getMoveToPosY();



        GameMsgProtocol.UserMoveToResult.Builder moveBuilder = GameMsgProtocol.UserMoveToResult.newBuilder();

        moveBuilder.setMoveUserId(userId);
        moveBuilder.setMoveFromPosX(cmd.getMoveFromPosX());
        moveBuilder.setMoveFromPosY(cmd.getMoveFromPosY());
        moveBuilder.setMoveToPosX(cmd.getMoveToPosX());
        moveBuilder.setMoveToPosY(cmd.getMoveToPosY());
        moveBuilder.setMoveStartTime(nowTime);

        // 构造结果并广播
        GameMsgProtocol.UserMoveToResult moveToResult = moveBuilder.build();
        Broadcast.broadcast(moveToResult);  //群发
    }
}
