package qinfeng.zheng.other;

/**
 * @Author ZhengQinfeng
 * @Date 2020/12/26 15:41
 * @dec DCL单例模式
 */
public class Code03_SingletonDcl {
    // volatile ： 保证可见性，禁止指令重排序
    private static volatile Code03_SingletonDcl instance;

    private Code03_SingletonDcl() {

    }


    public static Code03_SingletonDcl getInstance() {
        // 两次非空检测
        if (instance == null) {
            synchronized (Code03_SingletonDcl.class) {
                if (instance == null) {
                    instance = new Code03_SingletonDcl();
                }
            }
        }
        return instance;
    }

}
