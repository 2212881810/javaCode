package qinfeng.zheng.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/20 23:05
 * @dec
 */
public class ServerBio {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(9091, 20);


            System.out.println("new server ,port 9090");

            while (true) {

                Socket client = serverSocket.accept(); //阻塞 ，直到有客户端连接过来

                System.out.println("receive client : "+ client.getRemoteSocketAddress() + " receive  buffer size: " + client.getReceiveBufferSize());


                // 启动1个线程去处理client的连接

                new Thread(
                        ()->{
                            try {
                                InputStream is = client.getInputStream();
                                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                                while (true) {
                                    String readLine = reader.readLine();
                                    if (readLine != null) {
                                        System.out.println(readLine);
                                    } else {
                                        client.close();
                                        break;
                                    }
                                }
                                System.out.println("client closed.");

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                ).start();




            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
