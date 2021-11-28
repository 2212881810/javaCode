package qinfeng.zheng.jvm3;

/**
 * @Author ZhengQinfeng
 * @Date 2021/4/18 14:19
 * @dec
 */
public class Test {
    static {
        i = 0; // 可以对i进行赋值
//      System.out.println(i);//不能对i进行访问，编译报“非法向前引用”

    }

    static int i = 1;
}
