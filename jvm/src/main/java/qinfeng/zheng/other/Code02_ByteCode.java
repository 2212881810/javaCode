package qinfeng.zheng.other;

/**
 * @Author ZhengQinfeng
 * @Date 2020/12/26 14:46
 * @dec java 创建一个对象的过程
 *
 *
 *
 * 0 new #2 <java/lang/Object>
 * 3 dup
 * 4 invokespecial #1 <java/lang/Object.<init>>
 * 7 astore_1
 * 8 return
 *
 * 创建一个TT对象经历了以上五步， 4，7两步可能发生指令重排序， 这也是DCL在volatile的原因
 *
 *
 */
public class Code02_ByteCode {
    public static void main(String[] args) {
        TT tt = new TT();
    }


    private static class TT {
        int m;
    }
}

