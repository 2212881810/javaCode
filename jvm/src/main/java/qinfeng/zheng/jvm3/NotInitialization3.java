package qinfeng.zheng.jvm3;

/**
 * @Author ZhengQinfeng
 * @Date 2021/4/18 12:56
 * @dec
 */
public class NotInitialization3 {
    String name = "abc";
    public static void main(String[] args) {
        System.out.println(ConstantClass.value);
    }
}
