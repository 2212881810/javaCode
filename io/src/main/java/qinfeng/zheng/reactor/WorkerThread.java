package qinfeng.zheng.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/16 21:33
 * @dec 每个线程对应1个selector ,在高并发的情况下，多个客户端请求被分配到不同的selector上进行处理
 * <p>
 * 结论：每个客户端绑定1个selector
 *
 * 虽然这个类名叫着workerThread ,但是它不仅仅是字面意义上的worker group中的线程，同样也是boss group中的线程~
 *
 */
public class WorkerThread implements Runnable {

    Selector selector;
    /**
     * 此线程组即可能是boss线程组，也可能是worker线程组
     * 在调用WorkThreadGroup的构造方法后，如果是创建的boss group，那么此时的workers就是boss group;
     * 如果是创建的worker group , 那么此时的workers就是 worker group.
     *  WorkerThread  :   worker
     *  boss thread ---> boss group
     *  worker thread ----> worker group
     * =====================================
     *
     * 但是在执行完  registerSelector(server)方法之后，就修改了workers含义了，此时 :
     *
     * boss thread -----> worker group
     *
     */
    WorkThreadGroup workers;

    LinkedBlockingQueue<Channel> queue = new LinkedBlockingQueue<>();

   WorkerThread(WorkThreadGroup workers) {
        // 每个线程实例创建1个selector实例
        try {
            this.selector = Selector.open();

            this.workers = workers;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     *
     * run方法中执行流程分析如下：
     *
     * 对于boss group中的线程而言：
     * 1. new 1 个 WorkThreadGroup 对象bossGroup ,即boss线程组后，boss线程组中的线程就立马running , 不过会一直阻塞在select方法处
     * 2. bossGroup调用bind方法后，会将1个server注册到某个boss 线程的selector上，同时调用wakeup方法唤醒了select方法对selector的阻塞
     * 3. 注意，第2是要去注册，但是还没有真正将 注册server 到 selector 上的，所以run方法第1步的nums值为0 ,不会进行第2步操作，直接进入第3步操作
     * 4. run方法的第3步操作即是将server注册到某个boss线程的selector上去,完成1次while循环，代码又会阻塞在select方法处理
     * 5. 此时，如果某个client来连接这个server , 那么select被唤醒 ，run方法第1步的nums就等于1，然后执行第2步的逻辑
     * 6. 因为客户端连接事件，所以run方法的第2步会进入acceptHandler方法， 在该方法中获取的客户端连接对象client，并将这个client注册到某个worker线程组中selector上去
     *    ，具体的代码是“workers.registerSelector(client)” ，这样就完成boss线程接收客户端连接，worker线程完io的读写操作！
     *
     * 7. 调用workers.registerSelector(client)，即是在我们创建的workerGroup线程组中挑选出1个线程，并将这个client注册到这个线程的selector上去，同时wakeup这个selector
     *    ,此时又会进入run方法的第3步，将这个client注册到这个线程的selector上去，并注册读事件。 完成1次while循环，然后代码阻塞在select方法上
     *
     * 8.如果此时，客户端发送数据过来，selector被唤醒，nums == 1 ,进入run方法第2步， 执行readHandler方法，读写完成，再1次完成while循环，代码再1次阻塞到select方法上
     * 9.重复上面的逻辑....
     */
    @Override
    public void run() {
        // 单线程循环处理，accept , read , write 等事件， 这是串行操作的
        while (true) {

            try {
                // 第1步. 获取事件，如果没有事件注册到该selector上面，那么selector.select()会阻塞，直到有事件注册才会被唤醒
                // 当然也可以通过selector.wakeup()手动唤醒
                int nums = selector.select();

                // 第2步, 处理selectionKeys
                if (nums > 0) {
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = keys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();  // 清掉，免得重复操作
                        if (key.isAcceptable()) {  // 监听，接收客户端连接，然后需要将这个客户端注册到selector上
                            acceptHandler(key);
                        } else if (key.isReadable()) {
                            readHandler(key);
                        } else if (key.isWritable()) {
                            writeHandler(key);
                        }
                    }
                }


                // 第3步. 处理任务： 监听client
                if (!queue.isEmpty()) {
                    Channel channel = queue.take();
                    if (channel instanceof ServerSocketChannel) {  // 如果是server 端 注册accept事件
                        ServerSocketChannel server = (ServerSocketChannel) channel;
                        server.register(selector, SelectionKey.OP_ACCEPT);
                        System.out.println(Thread.currentThread().getName() + " register listen "  + server.getLocalAddress());

                    } else if (channel instanceof SocketChannel) { // 如果是client端 ，注册read事件

                        SocketChannel client = (SocketChannel) channel;
                        ByteBuffer buffer = ByteBuffer.allocateDirect(4096);
                        client.register(selector, SelectionKey.OP_READ, buffer);
                        System.out.println(Thread.currentThread().getName()+" register client: " + client.getRemoteAddress());
                    }


                }


            } catch (Exception e) {

            }

        }
    }

    private void writeHandler(SelectionKey key) {

    }

    private void readHandler(SelectionKey key) {
        System.out.println(Thread.currentThread().getName()+" read......");
        ByteBuffer  buffer = (ByteBuffer) key.attachment();
        SocketChannel client = (SocketChannel) key.channel();
        // 清1下，后面好读数据
        buffer.clear();

        while (true) {  // 循环读取数据到buffer中
            try {

                int lens = client.read(buffer);

                if (lens > 0) {
                    buffer.flip();// 翻转1下，将数据原样写回
                    while (buffer.hasRemaining()) {
                        client.write(buffer);  // client为什么即可以读，又可以写，因为他是1个channel
                    }
                    buffer.clear();
                } else if (lens == 0) {
                    break; // 数据读完
                } else {
                    // client 断开连接
                    System.out.println("client: " + client.getRemoteAddress()+"closed......");
                    key.cancel(); // 移除read事件
                    break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }



    }

    private void acceptHandler(SelectionKey key) {
        System.out.println(Thread.currentThread().getName() + "  acceptHandler...");

        ServerSocketChannel server = (ServerSocketChannel) key.channel();



        try {
            // 新的客户端连接
            SocketChannel client = server.accept();

            // 设置成非阻塞
            client.configureBlocking(false);

            // 选择1个selector ,并将这个client 注册到这个selector上面去
            workers.registerSelector(client);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setWorker(WorkThreadGroup workers) {
        this.workers = workers;
    }
}
