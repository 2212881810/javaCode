package com.qinfeng.zheng.cmd.handler;

import com.qinfeng.zheng.model.User;
import com.qinfeng.zheng.model.UserManager;
import com.qinfeng.zheng.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/23 10:17
 * @dec
 */
public class WhoElseIsHereCmdHandler  implements ICmdHandler<GameMsgProtocol.WhoElseIsHereCmd> {


    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.WhoElseIsHereCmd cmd) {

        if (ctx == null) {
            return;
        }

        GameMsgProtocol.WhoElseIsHereResult.Builder resultBuilder = GameMsgProtocol.WhoElseIsHereResult.newBuilder();
        for (User curUser : UserManager.listUser()) {

            if (curUser == null) {
                continue;
            }

            GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder userInfoBuilder = GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();
            userInfoBuilder.setUserId(curUser.getUserId());
            userInfoBuilder.setHeroAvatar(curUser.getHeroAvatar());


            // 构造移动状态
            GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.Builder moveStateBuilder =
                                GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.newBuilder();

            moveStateBuilder.setFromPosX(curUser.moveState.fromPosX);
            moveStateBuilder.setFromPosY(curUser.moveState.fromPosY);
            moveStateBuilder.setToPosX(curUser.moveState.toPosX);
            moveStateBuilder.setToPosY(curUser.moveState.toPosY);
            moveStateBuilder.setStartTime(curUser.moveState.startTime);
            userInfoBuilder.setMoveState(moveStateBuilder);

            resultBuilder.addUserInfo(userInfoBuilder);
        }


        GameMsgProtocol.WhoElseIsHereResult newResult = resultBuilder.build();
        ctx.writeAndFlush(newResult); // 发送给当前登录用户
    }
}
