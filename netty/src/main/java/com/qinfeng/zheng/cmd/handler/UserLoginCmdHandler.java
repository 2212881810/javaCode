package com.qinfeng.zheng.cmd.handler;

import com.qinfeng.zheng.Broadcast;
import com.qinfeng.zheng.login.LoginService;
import com.qinfeng.zheng.login.db.UserEntity;
import com.qinfeng.zheng.model.User;
import com.qinfeng.zheng.model.UserManager;
import com.qinfeng.zheng.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/23 20:09
 * @dec 用户登录
 */
public class UserLoginCmdHandler implements ICmdHandler<GameMsgProtocol.UserLoginCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserLoginCmd cmd) {
        if (ctx == null || cmd == null) {
            return;
        }

        String userName = cmd.getUserName();
        String password = cmd.getPassword();
        if (userName == null || password == null) {
            return;
        }

        // userLogin 回调函数
        LoginService.getInstance().userLogin(userName, password, userEntity -> {
            GameMsgProtocol.UserLoginResult.Builder builder = GameMsgProtocol.UserLoginResult.newBuilder();
            if (userEntity == null) {
                builder.setUserId(-1);
                builder.setUserName("");
                builder.setHeroAvatar("");
            } else {
                User newUser = new User();
                newUser.setUserId(userEntity.userId);
                newUser.setHeroAvatar(userEntity.heroAvatar);
                newUser.curHp = 100;
                newUser.userName = userEntity.userName;
                UserManager.addUser(newUser);

                // 将用户id 保存到session
                ctx.channel().attr(AttributeKey.valueOf("userId")).set(newUser.userId);

                builder.setUserName(userEntity.userName);
                builder.setHeroAvatar(userEntity.heroAvatar);
                builder.setUserId(userEntity.userId);
            }

            GameMsgProtocol.UserLoginResult loginResult = builder.build();
            ctx.writeAndFlush(loginResult);
            return null;
        });

    }
}
