package qinfeng.zheng.jvm3;

/**
 * @Author ZhengQinfeng
 * @Date 2021/4/18 12:32
 * @dec
 */
public class NotInitialization {
    public static void main(String[] args) {
        // 被动引用示例2
        SuperClass[] superClasses = new SuperClass[10];
    }
}
