package qinfeng.zheng.netty.v2;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import qinfeng.zheng.bio.User;

import java.io.*;
import java.net.InetSocketAddress;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/21 21:38
 * @dec  使用netty完成1个http协议的客户端 ，可以去访问nettyServer ,可以访问JettyServer,
 *      也可以去访问ServerBio,只不过ServerBio没有做http协议响应~
 */
public class NettyClient {
    public static void main(String[] args) throws InterruptedException, IOException {
        NioEventLoopGroup worker = new NioEventLoopGroup(1);

        Bootstrap b = new Bootstrap();

        ChannelFuture connect = b.group(worker)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {

                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new HttpClientCodec());
                        p.addLast(new HttpObjectAggregator(65535));
                        p.addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                // 接收服务端响应
                                FullHttpResponse response = (FullHttpResponse) msg;
                                ByteBuf content = response.content();
                                byte[] bytes = new byte[content.readableBytes()];
                                content.readBytes(bytes);

                                ByteArrayInputStream bis = new ByteArrayInputStream(bytes);

                                ObjectInputStream ois = new ObjectInputStream(bis);

                                User o = (User) ois.readObject();
                                System.out.println(o.getId()+","+o.getName());

                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                System.out.println(cause);
                            }
                        });


                    }
                }).connect(new InetSocketAddress("localhost", 9091));
        Channel clientChannel = connect.sync().channel();

        User user = new User(1, "root");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(user);

        byte[] bytes = out.toByteArray();
        System.out.println(bytes.length);

        ByteBuf byteBuf = Unpooled.copiedBuffer(bytes);

        DefaultFullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_0, HttpMethod.POST, "/", byteBuf);

        request.headers().set(HttpHeaderNames.CONTENT_LENGTH,bytes.length);
        clientChannel.writeAndFlush(request).sync(); // 作为客户端向server端发送数据
        System.out.println("send message to server success.");


    }
}
