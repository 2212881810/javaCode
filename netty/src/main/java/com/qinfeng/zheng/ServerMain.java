package com.qinfeng.zheng;

import com.qinfeng.zheng.handler.GameMsgHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/19 23:11
 * @dec
 */
public class ServerMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerMain.class);

    public static void main(String[] args) {
        PropertyConfigurator.configure(ServerMain.class.getClassLoader().getResourceAsStream("log4j.properties"));

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap sbs = new ServerBootstrap();

        sbs.group(bossGroup, workerGroup);
        sbs.channel(NioServerSocketChannel.class);
        sbs.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel sh) throws Exception {
                ChannelPipeline p = sh.pipeline();

                p.addLast(new HttpServerCodec(),
                        new HttpObjectAggregator(65535),
                        new WebSocketServerProtocolHandler("/websocket"),
                        new GameMsgHandler()
                );

            }
        });
        sbs.option(ChannelOption.SO_BACKLOG, 128);
        sbs.childOption(ChannelOption.SO_KEEPALIVE, true);

        ChannelFuture channelFuture = null;
        try {
            channelFuture = sbs.bind(12345).sync();

            if (channelFuture.isSuccess()) {
                LOGGER.info("游戏服务器启动成功~");
            }
            channelFuture.channel().closeFuture().sync(); // 阻塞 ，直到所有客户端都关闭
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }
}
