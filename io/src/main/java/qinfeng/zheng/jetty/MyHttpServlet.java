package qinfeng.zheng.jetty;

import qinfeng.zheng.bio.User;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/21 23:23
 * @dec
 */
public class MyHttpServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletInputStream inputStream = req.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(inputStream);

        try {
            User o = (User) ois.readObject();
            System.out.println(o.getId() + " " + o.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        // 下面是响应

        ServletOutputStream out = resp.getOutputStream();

        ObjectOutputStream oos = new ObjectOutputStream(out);

        oos.writeObject(new User(1, "admin"));

    }
}
