package qinfeng.zheng.util;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CountDownLatch;
/**
 * @Author ZhengQinfeng
 * @Date 2021/2/9 22:32
 * @dec  默认的watch,用户监听client连接zk server
 */

public class DefaultWatch  implements Watcher {

    CountDownLatch cc ;

    public void setCc(CountDownLatch cc) {
        this.cc = cc;
    }

    @Override
    public void process(WatchedEvent event) {
//        System.out.println(event.toString());

        switch (event.getState()) {
            case Unknown:
                break;
            case Disconnected:
                break;
            case NoSyncConnected:
                break;
            case SyncConnected:
                System.out.println("connect to zk server");
                // 连接成功
                cc.countDown();
                break;
            case AuthFailed:
                break;
            case ConnectedReadOnly:
                break;
            case SaslAuthenticated:
                break;
            case Expired:
                break;
            case Closed:
                break;
        }


    }
}
