package qinfeng.zheng.code01;

/**
 * @Author ZhengQinfeng
 * @Date 2021/3/27 22:42
 * @dec
 */
public class T002_ClassLoader extends ClassLoader{
    public static void main(String[] args) {
        System.out.println(getSystemClassLoader());  // 默认的classLoader

        System.out.println(Thread.currentThread().getContextClassLoader());

        System.out.println(String.class.getClassLoader());
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        //



        return super.findClass(name);
    }
}
