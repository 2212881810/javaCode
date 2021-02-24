package qinfeng.zheng.configcenter;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * @Author ZhengQinfeng
 * @Date 2021/2/9 22:49
 * @dec 主要逻辑实现类，Reactor编程模型~~~
 */
public class ConfigDataWatch implements Watcher, AsyncCallback.StatCallback, AsyncCallback.DataCallback {
    private ConfigData data;


    private ZooKeeper zooKeeper;

    private CountDownLatch cc = new CountDownLatch(1);

    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    public ConfigData getData() {
        return data;
    }

    public void setData(ConfigData data) {
        this.data = data;
    }

    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case None:
                break;
            case NodeCreated:
                //1. 如果data节点创建成功了，就通过getData从/configCenter/data去获取配置数据
                // 这里通过AsyncCallback.DataCallback 异步回调的方式获取数据--->processResult
                this.getConfigData();
                break;
            case NodeDeleted:
                //3.错误处理
                data.setData("");
                cc = new CountDownLatch(1);
                break;
            case NodeDataChanged:
                //2. 如果数据发生了改变，同样通过getData方法从zkServer中去获取数据
                this.getConfigData();
                break;
            case NodeChildrenChanged:
                break;
            case DataWatchRemoved:
                break;
            case ChildWatchRemoved:
                break;
            case PersistentWatchRemoved:
                break;
        }
    }




    public void await() {
        //  判断/data这个节点是否存在
        zooKeeper.exists("/data", this, this, "xxxx");

        try {
            // 阻塞
            cc.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * 判断data节点是否存在
     * @param i
     * @param s
     * @param o
     * @param stat
     */
    @Override
    public void processResult(int i, String s, Object o, Stat stat) {
        if (stat != null) {  // 说明/data节点已存在
           this.getConfigData();
        }
    }

    private void getConfigData() {
        zooKeeper.getData("/data", this, this, "xxxx");
    }

    /**
     * 异步回调，从zkServer中获取数据
     * @param i
     * @param s
     * @param o
     * @param bytes ： 数据
     * @param stat
     */
    @Override
    public void processResult(int i, String s, Object o, byte[] bytes, Stat stat) {
        if (bytes != null) {
            String configData = new String(bytes);
            data.setData(configData);
            cc.countDown();
        }

    }
}
