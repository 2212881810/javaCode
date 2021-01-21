package qinfeng.zheng.rpc;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/18 23:26
 * @dec 对象序列化工具类
 */
public class SerUtil {

    static ByteArrayOutputStream out = new ByteArrayOutputStream();

    public synchronized static byte[] ser(Object obj) {
        byte[] ret = null;
        try {
            out.reset();  // 重置，清空
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(obj);
            ret = out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}
