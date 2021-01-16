package qinfeng.zheng.jol;


import org.openjdk.jol.info.ClassLayout;

/**
 * @Author ZhengQinfeng
 * @Date 2020/12/26 0:07
 * @dec  测试对象 markword
 */
public class Code01_Jol {
    public static void main(String[] args) {
        System.out.println(123);
        Student student = new Student();

        System.out.println(ClassLayout.parseInstance(student).toPrintable());
    }

    private static class Student {
         Long id;
    }

}
