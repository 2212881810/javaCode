package qinfeng.zheng.jetty;


import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.net.InetSocketAddress;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/21 22:23
 * @dec  MyHttpServlet ： 这个类必须是public修饰符去修饰，不然直接访问报错！~
 */
public class JettyServer {
    public static void main(String[] args) {
        Server server = new Server(new InetSocketAddress("localhost", 9091));

        ServletContextHandler context = new ServletContextHandler(server, "/");
        server.setHandler(context);
        context.addServlet(MyHttpServlet.class, "/*");
        try {
            server.start();
            server.join();// 卡着，别停
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

