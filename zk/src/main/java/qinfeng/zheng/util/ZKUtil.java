package qinfeng.zheng.util;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @Author ZhengQinfeng
 * @Date 2021/2/9 22:31
 * @dec  zk工具类，用于创建zk连接和关闭zk连接
 */
public class ZKUtil {
    // zk集群的连接地址 ， configCenter为配置中心的根地址，client连接之前，需要手动创建好
    private static String zkPath = "192.168.79.70:2181,192.168.79.70:2181,192.168.79.70:2181/configCenter";
    private static ZooKeeper zooKeeper = null;

    private static CountDownLatch cc = new CountDownLatch(1);
    private static DefaultWatch dw = new DefaultWatch();

    /**
     * 创建zk连接
     * @return
     */
    public static ZooKeeper getZK() {

        try {
            zooKeeper = new ZooKeeper(zkPath, 1000, dw);
            dw.setCc(cc);
            // 阻塞，直到zkClient连接zkServer成功
            cc.await();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return zooKeeper;
    }

    /**
     * 关闭zk
     */
    public static void closeZK() {
        try {
            zooKeeper.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
