package qinfeng.zheng.netty.v1;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.EventExecutorGroup;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/17 20:32
 * @dec
 */
public class MyNetty {
    /**
     * 创建客户端给别人发消息
     *
     * @throws Exception
     */
    @Test
    public void testLoopExec() throws Exception {
        // 就是1个线程池
        NioEventLoopGroup group = new NioEventLoopGroup(1);
        group.execute(() -> {
                while (true) {
                    System.out.println("hello1~");
//                    TimeUnit.SECONDS.sleep(1);
                }

        });

//        group.execute(() -> {
//            try {
//                while (true) {
//                    System.out.println("hello2~");
//                    TimeUnit.SECONDS.sleep(1);
//
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
    }


    /**
     * 可以使用nc -l启动1个server
     * <p>
     * nc -l 192.125.68.12 9090
     *
     * @throws Exception
     */
    @Test
    public void testClientMode() throws Exception {
        // 得到1个线程，其实就是1个多路复用器~
        NioEventLoopGroup thread = new NioEventLoopGroup(1);

        NioSocketChannel client = new NioSocketChannel();
        thread.register(client);

        // 响应式的： MyInHandler不是马上就要执行， 他会等server发消息过来时，才会执行
        ChannelPipeline pipeline = client.pipeline();
        pipeline.addLast(new MyInHandler());


        // reactor : 异步操作
        // connect是异步操作
        ChannelFuture connect = client.connect(new InetSocketAddress("127.0.0.1", 9090));
        // 连接成功
        ChannelFuture sync = connect.sync();
        ByteBuf byteBuf = Unpooled.copiedBuffer("hello server".getBytes());

        // write是异步操作
        ChannelFuture send = client.writeAndFlush(byteBuf);
        // 写成功
        send.sync();

        sync.channel().closeFuture().sync();  // 阻塞，等待server关闭连接
        System.out.println("client over 。。。。");


    }


    @Test
    public void testServerMode() throws Exception {
        NioEventLoopGroup thread = new NioEventLoopGroup(1);

        NioServerSocketChannel server = new NioServerSocketChannel();


        //将server注册到多路复用器上
        thread.register(server);

        // 响应式： 指不定什么时候客户端来请求了

        ChannelPipeline pipeline = server.pipeline();


        pipeline.addLast(new MyAcceptHandler(thread, new MyInHandler()));  // 完成两件事： 1， 接收客户端 ；2 ，注册客户端到多路复用器

        // 异步的
        ChannelFuture bind = server.bind(new InetSocketAddress("192.168.1.28", 9090));

        bind.sync().channel().closeFuture().sync();  // 阻塞 ，直到客户端关闭

        System.out.println("server close....");

    }

    @ChannelHandler.Sharable  // 如果不加这个注册， 只能被1个连接使用~
    private class MyInHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            System.out.println("client registered。。。");
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("client active。。。");
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf byteBuf = (ByteBuf) msg;
            // 使用readXX方法时，会移动buf中readIndex指针， 所以，此时可以使用getXX
//            CharSequence str = byteBuf.readCharSequence(byteBuf.readableBytes(), CharsetUtil.UTF_8);
            // getCharSequence 不会移动buf中read 指针， 自定义get的起始位置
            CharSequence str = byteBuf.getCharSequence(0, byteBuf.readableBytes(), CharsetUtil.UTF_8);
            System.out.println(str);

            // 将接收到消息返回到server
            ctx.writeAndFlush(byteBuf); // 如果前面使用readCharSequence 读取buf中的数据 ，那么此时writeAndFlush时，buf为空~！read时指针移到最后？？？？？


            /*
                netty : read, get是一对Api, write ,set 是1对Api
                        read ,write 会移动指针；get, set 不会移动指针
             */
        }
    }

    private class MyAcceptHandler extends ChannelInboundHandlerAdapter {

        private final NioEventLoopGroup selector;
        private final ChannelHandler handler;

        public MyAcceptHandler(NioEventLoopGroup thread, ChannelHandler handler) {
            this.selector = thread;
            this.handler = handler;
        }

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            System.out.println("server registered。。。");
        }


        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            // 1. 得到1个客户端
            SocketChannel client = (SocketChannel) msg;  //相当调用NIO 中 ServerSocket 中的accept . 得到client


            // 2, 注册，

            selector.register(client);  // 将client注册到多路复用器上
            // 3. 响应式的handler

            ChannelPipeline pipeline = client.pipeline();
            pipeline.addLast(handler);

            /*

            NIO：　 server 端 只会干两件事。 其1是 listen socket , accept 得到1个client  ; 其2是普通socket ,进行读写
             */


        }
    }


    /**
     * netty 标准版本的客户端代码 ~
     *
     * @throws Exception
     */
    @Test
    public void testNettyClient() throws Exception {

        NioEventLoopGroup group = new NioEventLoopGroup(1);

        Bootstrap bs = new Bootstrap();
        ChannelFuture connect = bs.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new MyInHandler());
                    }
                }).connect(new InetSocketAddress("127.0.0.1", 9090));

        Channel client = connect.sync().channel();
        ByteBuf byteBuf = Unpooled.copiedBuffer("hello server ".getBytes());
        ChannelFuture send = client.writeAndFlush(byteBuf);
        send.sync();

        client.closeFuture().sync();

    }

    @Test
    public void testNettyServer() throws Exception {

        NioEventLoopGroup group = new NioEventLoopGroup(1);


        ServerBootstrap bs = new ServerBootstrap();

        ChannelFuture bind = bs.group(group, group)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new MyInHandler());
                    }
                })
                .bind(new InetSocketAddress("192.168.1.28", 9090));

        bind.sync().channel().closeFuture().sync();

    }
}
