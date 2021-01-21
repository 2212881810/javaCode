package qinfeng.zheng.rpc;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.junit.Test;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.TypeVariable;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/17 23:01
 * @dec // todo 207
 */
public class MyRPC {


    @Test
    public void serverStart() throws Exception {

        MyCar car = new MyCar();
        MyCat cat = new MyCat();
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.register(Cat.class.getName(), cat);
        dispatcher.register(Car.class.getName(), car);

        NioEventLoopGroup boss = new NioEventLoopGroup(50);
        NioEventLoopGroup worker = boss;
        ServerBootstrap sbs = new ServerBootstrap();

        ChannelFuture bind = sbs.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
//                        System.out.println("server accept client port :" + ch.remoteAddress().getPort());
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new ServerDecode());
                        pipeline.addLast(new ServerRequestHandler(dispatcher));
                    }
                }).bind(new InetSocketAddress("localhost", 9090));
        System.out.println("server running....");
        bind.sync().channel().closeFuture().sync();

    }


    /**
     * 模拟consumer
     *
     * @throws Exception
     */
    @Test
    public void startClient() throws Exception {
        new Thread(() -> {
            try {
                serverStart();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        Thread.sleep(2000);

        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            threads[i] = new Thread(() -> {
                Car car = proxyGet(Car.class);
                String param = "hello car" + finalI;
                String s = car.create(param);
                System.out.println("入参：" + param + " 响应值：" + s);

            });

        }


        for (Thread thread : threads) {
            thread.start();
        }
        System.in.read();

    }

    public static <T> T proxyGet(Class<T> interfaceInfo) {
        // 动态代理
        ClassLoader loader = interfaceInfo.getClassLoader();
        Class<?>[] methodInfo = {interfaceInfo};

        return (T) Proxy.newProxyInstance(loader, methodInfo, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //如何设计consumer对于provider 的调用过程
                //1. 调用服务 ，方法，参数 --》 封装成message [content]
                String interfaceName = interfaceInfo.getName();  // 服务， 接口名称
                String methodName = method.getName();

                Class<?>[] parameterTypes = method.getParameterTypes();

                MyContent content = new MyContent();
                content.setArgs(args);
                content.setInterfaceName(interfaceName);
                content.setParameterTypes(parameterTypes);
                content.setMethodName(methodName);

                // 转成byte数组
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ObjectOutputStream oout = new ObjectOutputStream(out);
                oout.writeObject(content);
                byte[] msgBody = out.toByteArray();

                //2. requestId + message , 本地缓存
                // 协议： 【header】 【body】
                MyHeader header = createHeader(msgBody);
                out.reset();
                oout = new ObjectOutputStream(out);
                oout.writeObject(header);
                byte[] msgHeader = out.toByteArray();

//                System.out.println("header size :"+msgHeader.length);
                //3. 连接池，取得连接

                NioSocketChannel clientChannel = ClientFactory.getInstance().getClient(new InetSocketAddress("localhost", 9090));

                // 4. 发送，走IO, ---> Netty (event 驱动)
                long requestID = header.getRequestID();
          /*
                  使用这种方式不能获取返回值
                CountDownLatch countDownLatch = new CountDownLatch(1);
                RequestMapping.addCallBack(requestID, new Runnable() {
                    @Override
                    public void run() {
                        countDownLatch.countDown();
                    }
                });*/


                CompletableFuture<String> cf = new CompletableFuture<>();
                RequestMapping.addCallBack(requestID, cf);

                ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(msgHeader.length + msgBody.length);
                byteBuf.writeBytes(msgHeader);
                byteBuf.writeBytes(msgBody);

                ChannelFuture channelFuture = clientChannel.writeAndFlush(byteBuf);

                channelFuture.sync();// 同步

                // 5. provider 响应，如何控制， 让代码停在这儿，等响应内容 ，然后继续执行。。。
//                countDownLatch.await();


                return cf.get(); // 阻塞的， 直到 cf.complete("有值了。。。。");
            }
        });

    }


    private static MyHeader createHeader(byte[] msgBody) {
        MyHeader header = new MyHeader();
        int size = msgBody.length;

        // 通过1的位置 来标识请求的状态
        int f = 0x14141414;
        // 0x14 [16进制] == 0001 0100 [二进制]     // 4个8位 =  32位
        header.setFlag(f);
        header.setDataLen(size);
        long requestId = Math.abs(UUID.randomUUID().getLeastSignificantBits());
        header.setRequestID(requestId);
        return header;
    }
}

/**
 * 解码器
 */
class ServerDecode extends ByteToMessageDecoder {
    // 父类里一定有channelRead , 面是这个channelRead里面会调用这个decode方法，
    // 处理前1次channelRead留存的buf中的数据，其实会将前1次read留存的buf数据 拼接到本次read的buf的前面
    // 两次read的数据一定是完整的
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
//        System.out.println("server channel start :" + buf.readableBytes());

        // 通信协议
        while (buf.readableBytes() > 96) {
            byte[] bytes = new byte[96];
//            buf.readBytes(bytes);  // 使用readXX方法，读指针会移动
            buf.getBytes(buf.readerIndex(), bytes);  // get方法，readIndex不会移动；从buf.readerIndex()开始读
            ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bi);
            MyHeader header = (MyHeader) ois.readObject();
//            System.out.println("server accept request id :" + header.getRequestID());

            if (buf.readableBytes() - 96 >= header.getDataLen()) {  // 剩余buf中可读字节数大于content的length
                buf.readBytes(96); // 移动readIndex , 不读取数据
                byte[] data = new byte[(int) header.getDataLen()];
                buf.readBytes(data);
                ByteArrayInputStream bais = new ByteArrayInputStream(data);
                ObjectInputStream oiss = new ObjectInputStream(bais);
                if (header.getFlag() == 0x14141414) {  // 请求
                    MyContent content = (MyContent) oiss.readObject();
                    out.add(new PackageMsg(header, content));
                } else if (header.getFlag() == 0x14141424) {  // 响应
                    MyContent content = (MyContent) oiss.readObject();
                    out.add(new PackageMsg(header, content));
                }

            } else {
//                System.out.println("server channel else :" + buf.readableBytes());
                break;  //  如果buf中剩余的数据不够拼1个body，直接break ,等到下1次read时处理~
            }
        }

    }
}


class PackageMsg {
    MyHeader header;
    MyContent content;

    public PackageMsg(MyHeader header, MyContent content) {
        this.header = header;
        this.content = content;
    }


    public MyHeader getHeader() {
        return header;
    }

    public void setHeader(MyHeader header) {
        this.header = header;
    }

    public MyContent getContent() {
        return content;
    }

    public void setContent(MyContent content) {
        this.content = content;
    }
}

class ServerRequestHandler extends ChannelInboundHandlerAdapter {


    Dispatcher dispatcher;


    public ServerRequestHandler(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    /*
            netty channelRead 不能保证数据的完整性，而且不是1次read处理一个message , 前后两次read能保证数据的完整性
         */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 因为被ServerCoder解码器处理了，所以这里的msg不再是ByteBuf类型，而是解码出out的类型
//        ByteBuf buf = (ByteBuf) msg;

        PackageMsg requestPkg = (PackageMsg) msg;

//        System.out.println(requestPkg.getContent().getInterfaceName() + "  参数：" + Arrays.toString(requestPkg.getContent().getArgs()));

        // 假设处理完了，返回数据给客户端~
        // 如何操作~


//        System.err.println("============server 端处理响应内容=======================");
        /*
        1. ByteBuf
        2. requestId
        3. client端解码问题
        4. 关注通信协议 来的时候flag : Ox14141414 返回可以是0x14141424
               有新的header ,content
         */

        /*
        有以下几种方式 ：
        1. 直接在当前方法处理IO和业务和返回，这样下1次read等到这1次read完成之后才执行【阻塞，代码略】
        2. 使用netty自己的eventLoop来处理业务及返回
        3. 自定义线程池
         */
        // io线程
        String ioThreadName = Thread.currentThread().getName();
        // 使用eventLoop实现
//        ctx.executor().execute(new Runnable() {  // 第2种方式的第1种实现方式： 处理业务和返回与io read是同1个线程， 但是它是将业务和返回封装成了1个task，丢到后面去处理，与传统的阻塞不同
        ctx.executor().parent().next().execute(new Runnable() {  // 第2种方式的第2种实现方式，丢到另外1个eventloopgroup中去执行
            // 通过打印可以明显看到线程的不一样~！
            @Override
            public void run() {
                // 执行线程
                String execThreadName = Thread.currentThread().getName();

//                System.out.println("ioThreadName == execThreadName : " + ioThreadName.equals(execThreadName));
                // 自定义1个响应数据
                String responseMsg = "io thread is " + ioThreadName + " , exec thread " + execThreadName + " from argus :" + requestPkg.getContent().getArgs()[0];
//                System.out.println(responseMsg);


                String serviceName = requestPkg.getContent().getInterfaceName();
                String methodName = requestPkg.getContent().getMethodName();

                Object c = dispatcher.get(serviceName);
                Class<?> clzss = c.getClass();
                Object res = null;
                try {
                    Method method = clzss.getMethod(methodName, requestPkg.getContent().getParameterTypes());
                    res = method.invoke(c, requestPkg.getContent().getArgs());
                } catch (Exception e) {
                    e.printStackTrace();
                }


                MyContent content = new MyContent();
                content.setResponse((String) res);

                byte[] contentByte = SerUtil.ser(content);


                MyHeader responseHeader = new MyHeader();
                responseHeader.setRequestID(requestPkg.getHeader().getRequestID());
                responseHeader.setFlag(0x14141424);
                responseHeader.setDataLen(contentByte.length);


                byte[] headerByte = SerUtil.ser(responseHeader);


                ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(contentByte.length + headerByte.length);
                byteBuf.writeBytes(headerByte);
                byteBuf.writeBytes(contentByte);
                ctx.writeAndFlush(byteBuf);  // 写到客户端接收~，客户端也需要解码
            }
        });


        //原内容返回
//        ByteBuf sendBuf = buf.copy();
//
//
//        ChannelFuture channelFuture = ctx.writeAndFlush(sendBuf);
//        channelFuture.sync(); //同步，等它完成
    }
}

class RequestMapping {

    static ConcurrentHashMap<Long, CompletableFuture> mapping = new ConcurrentHashMap<>();

    public static void addCallBack(Long requestId, CompletableFuture cb) {
        mapping.putIfAbsent(requestId, cb);
    }

    public static void runCallBack(PackageMsg msg) {
        CompletableFuture cf = mapping.get(msg.getHeader().getRequestID());
        cf.complete(msg.getContent().getResponseMsg());

        mapping.remove(msg.getHeader().getRequestID());
    }
}

class ClientFactory {
    int poolSize = 5;
    // 1个consumer 可能连接很多的provider ,每个provider都有自己的pool, [k,v]
    ConcurrentHashMap<InetSocketAddress, ClientPool> outBoxes = new ConcurrentHashMap<>();

    NioEventLoopGroup clientWorkerGroup;
    Random random = new Random();
    private static final ClientFactory factory;

    static {
        factory = new ClientFactory();
    }

    public static ClientFactory getInstance() {
        return factory;
    }

    public synchronized NioSocketChannel getClient(InetSocketAddress address) {
        ClientPool clientPool = outBoxes.get(address);
        if (clientPool == null) {
            outBoxes.putIfAbsent(address, new ClientPool(poolSize));
            clientPool = outBoxes.get(address);
        }

        int index = random.nextInt(poolSize);

        if (clientPool.clients[index] != null && clientPool.clients[index].isActive()) {
            return clientPool.clients[index];
        }

        synchronized (clientPool.locks[index]) {
            return clientPool.clients[index] = create(address);
        }

    }

    private NioSocketChannel create(InetSocketAddress address) {
        // 基于Netty 的客户端创建 方式
        clientWorkerGroup = new NioEventLoopGroup(20);
        Bootstrap bs = new Bootstrap();
        ChannelFuture connect = bs.group(clientWorkerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new ServerDecode());  // 因为请求、响应使用的是同一种协议，所以可以使用同1种解码器
                        pipeline.addLast(new ClientResponseHandler()); // 解决给谁的
                    }
                }).connect(address);

        try {
            NioSocketChannel client = (NioSocketChannel) connect.sync().channel();
            return client;
        } catch (InterruptedException e) {

            e.printStackTrace();
        }


        return null;
    }
}

class ClientResponseHandler extends ChannelInboundHandlerAdapter {
    // consumer读取server端响应的回来的内容
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ByteBuf buf = (ByteBuf) msg;  // header , content都在buf中
//        if (buf.readableBytes() > 96) {
//            byte[] bytes = new byte[96]; // 先读header
//            buf.readBytes(bytes);
//            ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
//            ObjectInputStream ois = new ObjectInputStream(bi);
//            MyHeader header = (MyHeader) ois.readObject();
//            System.out.println("client request id :" + header.getRequestID());
//            RequestMapping.run(header.getRequestID());
//        }

        //上面解码的过程 由ServerDecode处理器完成了


        PackageMsg pagMsg = (PackageMsg) msg;
        RequestMapping.runCallBack(pagMsg);
    }
}

class ClientPool {
    NioSocketChannel[] clients;
    Object[] locks;

    ClientPool(int size) {
        this.clients = new NioSocketChannel[size];  // init , 但是连接还没有创建
        this.locks = new Object[size]; //初始化锁
        for (int i = 0; i < size; i++) {
            locks[i] = new Object();
        }
    }


}


class MyHeader implements Serializable {
    // 定义通信协议
    int flag; // int 32 bit位，可以设置信息
    long requestID;
    long dataLen;

    // 32 + 64  + 64 = 160

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public long getRequestID() {
        return requestID;
    }

    public void setRequestID(long requestID) {
        this.requestID = requestID;
    }

    public long getDataLen() {
        return dataLen;
    }

    public void setDataLen(long dataLen) {
        this.dataLen = dataLen;
    }
}

class MyContent implements Serializable {
    String interfaceName;
    String methodName;
    Class<?>[] parameterTypes;
    Object[] args;

    String responseMsg;

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public void setResponse(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    public String getResponseMsg() {
        return responseMsg;
    }
}


interface Car {
    String create(String msg);
}

class MyCar implements Car {

    @Override
    public String create(String msg) {
        System.out.println("MyCar get client args : " + msg);
        return "my car response " + msg;
    }
}

interface Cat {
    void create(String msg);
}

class MyCat implements Cat {

    @Override
    public void create(String msg) {
        System.out.println("MyCat get client args : " + msg);
    }
}

class Dispatcher {
    private static ConcurrentHashMap<String, Object> invokeMap = new ConcurrentHashMap<>();

    public void register(String k, Object v) {
        invokeMap.put(k, v);
    }

    public Object get(String key) {
        return invokeMap.get(key);
    }
}