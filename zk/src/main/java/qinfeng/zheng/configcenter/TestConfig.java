package qinfeng.zheng.configcenter;

import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import qinfeng.zheng.util.ZKUtil;

import java.util.concurrent.TimeUnit;

/**
 * @Author ZhengQinfeng
 * @Date 2021/2/9 22:46
 * @dec 测试类
 */
public class TestConfig {

    private ZooKeeper zooKeeper;

    @Before
    public void before() {
        zooKeeper = ZKUtil.getZK();
    }

    @After
    public void after() {
        ZKUtil.closeZK();
    }


    @Test
    public void getConfig() {
        // 使用reactor编程模型...
        ConfigDataWatch watch = new ConfigDataWatch();

        ConfigData data = new ConfigData();
        // ConfigData对象用于数据交互
        watch.setData(data);
        // zookeeper对象用于与zkServer数据交互
        watch.setZooKeeper(zooKeeper);

        // 阻塞，
        watch.await();

        while (true) {
            if (data.getData().equals("")) {
                System.out.println("配置数据丢了...");
                watch.await();
            } else {
                System.out.println(data.getData());
            }

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
