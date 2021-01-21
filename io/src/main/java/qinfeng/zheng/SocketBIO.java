package qinfeng.zheng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/16 20:40
 * @dec
 */
public class SocketBIO {
    public static void main(String[] args) throws IOException {

        ServerSocket server = new ServerSocket(9090, 20);

        System.out.println("创建socket server " + server.getLocalPort());

        while (true) {
            //如果没有客户端连接过来，accept方法会一直阻塞
            Socket socket = server.accept();
            System.out.println("来了一个客户端："+ socket.getRemoteSocketAddress());

            // 启动一个线程去读取客户端发送过来的数据
            new Thread(()->{
                try {
                    InputStream is = socket.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    while (true) {
                        //该方法也是阻塞的
                        String readLineData = reader.readLine();
                        if (readLineData != null) {
                            System.out.println(readLineData);
                        } else {
                            // 关闭连接
                            socket.close();
                            break;
                        }
                    }
                    System.out.println(socket.getRemoteSocketAddress() + " 断开连接....");
                } catch (Exception e) {

                }

            }).start();
        }


    }
}
