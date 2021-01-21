package qinfeng.zheng.netty.v2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import qinfeng.zheng.bio.User;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/20 23:26
 * @dec 使用netty完成了1个简单的http服务器
 */
public class NettyServer {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = boss;
        ServerBootstrap b = new ServerBootstrap();

        ChannelFuture bind = b.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();

                        p.addLast(new HttpServerCodec());// netty 提价的关于http的编解码handler
                        p.addLast(new HttpObjectAggregator(65535));  // 聚合器
                        p.addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                               // 前面两个handler处理之后，msg 成了1个httprequest
                                FullHttpRequest request = (FullHttpRequest) msg;
                                System.out.println(request);

                                // 就是client发送的user
                                ByteBuf content = request.content();

                                byte[] bytes = new byte[content.readableBytes()];
                                content.readBytes(bytes);

                                //反序列化
                                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));

                                User user = (User) ois.readObject();
                                System.out.println("id :"  + user.getId()+ ", name: " + user.getName());


                                System.out.println("========================");

//                                DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_0, HttpResponseStatus.OK,true,true);

                                // 构造http协议响应回去
                                DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_0,
                                                                    HttpResponseStatus.OK,
                                                                    Unpooled.copiedBuffer(bytes));

                                response.headers().set(HttpHeaderNames.CONTENT_LENGTH,bytes.length);


                                ctx.writeAndFlush(response);



                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                System.out.println(cause);
                            }
                        });

                    }
                }).bind(new InetSocketAddress("localhost", 9091));

        try {
            System.out.println("server start 。。。");
            bind.sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        boss.shutdownGracefully();

    }
}
