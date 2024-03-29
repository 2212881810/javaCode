package qinfeng.zheng.service;

import junit.framework.TestCase;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @Author ZhengQinfeng
 * @Date 2021/3/31 23:32
 * @dec
 */
@RunWith(JMockit.class)
public class HelloJMockitTest  {

    public void testSayHello() {
    }
    // 这是一个测试属性
    @Mocked
    private HelloJMockit helloJMockit;

    @Test
    public void test1() {
        System.out.println(helloJMockit);
        // 录制(Record)
        new Expectations() {
            {
                helloJMockit.sayHello();
                // 期待上述调用的返回是"hello,david"，而不是返回"hello,JMockit"
                result = "hello,david";
            }
        };
        // 重放(Replay)
//        String msg = helloJMockit.sayHello();
//        Assert.assertTrue(msg.equals("hello,david"));
//        // 验证(Verification)
//        new Verifications() {
//            {
//                helloJMockit.sayHello();
//
//                times = 1;
//            }
//        };
    }

    @Test
    public void test2(@Mocked HelloJMockit helloJMockit /* 这是一个测试参数 */) {
        // 录制(Record)
        new Expectations() {
            {
                helloJMockit.sayHello();
                // 期待上述调用的返回是"hello,david"，而不是返回"hello,JMockit"
                result = "hello,david";
            }
        };
        // 重放(Replay)
        String msg = helloJMockit.sayHello();
        Assert.assertTrue(msg.equals("hello,david"));
        // 验证(Verification)
        new Verifications() {
            {
                helloJMockit.sayHello();
                // 验证helloJMockit.sayHello()这个方法调用了1次
                times = 1;
            }
        };
    }


}