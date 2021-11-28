package qinfeng.zheng.code01;

/**
 * @Author ZhengQinfeng
 * @Date 2021/3/27 23:05
 * @dec
 */
public class T003_Initialization {
    public  static void main(String[] args) {
        System.out.println(XXX.count);

        for (int i = 0; i < 100_100000; i++) {

        }
    }

    void m() {
        synchronized (this) {

        }
    }

    static synchronized void n() {

    }

    synchronized void l() {

    }

}

class XXX {
    public static int count = 2;
    public static XXX x = new XXX();

    private XXX() {
        count++;
    }
}