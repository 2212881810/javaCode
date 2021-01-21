package qinfeng.zheng;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/16 20:58
 * @dec client，用于发送数据
 */
public class SocketClient {
    public static void main(String[] args) throws Exception {
        Socket client = new Socket("192.168.79.199", 9090);

//        client.setSendBufferSize(4096);
        client.setSendBufferSize(10240);
        client.setTcpNoDelay(true);

        System.out.println("启动一个客户端："+ client.getLocalPort());
        OutputStream outputStream = client.getOutputStream();

        while (true) {
            outputStream.write("this is test content\n".getBytes());
            outputStream.flush();
            TimeUnit.SECONDS.sleep(1);
        }


//        InputStream in = System.in;
//        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//        while (true) {
//            String line = reader.readLine();
//
//            if (line != null) {
//                byte[] bDatas = line.getBytes();
//
//                for (byte b : bDatas) {
//                    outputStream.write(b);  // 一个字节一个字节的写
//                }
//                outputStream.flush();
//            }
//        }
    }
}
