package qinfeng.zheng.socket;

import java.io.*;
import java.net.Socket;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/21 23:37
 * @dec
 */
public class SocketClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("192.168.79.199", 9090);

        socket.setTcpNoDelay(false);
        socket.setSendBufferSize(20);
        OutputStream os = socket.getOutputStream();

        InputStream in = System.in;
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        while(true){
            String line = reader.readLine();
            if(line != null ){
                byte[] bb = line.getBytes();
                for (byte b : bb) {
                    os.write(b);
                }
            }
        }
    }
}
