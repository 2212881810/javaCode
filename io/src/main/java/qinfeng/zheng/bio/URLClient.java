package qinfeng.zheng.bio;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/20 23:12
 * @dec 使用URL完成http请求、响应
 */
public class URLClient {
    public static void main(String[] args) throws IOException {
//        URL url = new URL("http://localhost:9090/");  // 请头体信息，请求的是ServerBio这个类
        URL url = new URL("http://localhost:9091/");  // 请头体信息 ，请求是NettyServer这个类
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true); // 请求可以携带请求体
        connection.setDoOutput(true);  // 响应可以携带响应体

        connection.setRequestMethod("POST");
        OutputStream out = connection.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(new User(1, "admin"));  // 请求体，写出去




        if (connection.getResponseCode() == 200) {  // 会1直阻塞 ， 因为ServerBio 没有响应数据 ， 但是ServerBio 会接收到这边发送过去的请求头信息
            InputStream in = connection.getInputStream();
            ObjectInputStream oin = new ObjectInputStream(in);
            User user = null;
            try {
                user = (User) oin.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            System.out.println(user.getId()+"," + user.getName());
        }

//        System.in.read();
    }
}
