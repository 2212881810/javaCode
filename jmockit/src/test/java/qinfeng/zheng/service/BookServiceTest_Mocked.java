package qinfeng.zheng.service;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import qinfeng.zheng.entry.Book;

/**
 * @Author ZhengQinfeng
 * @Date 2021/3/31 23:12
 * @dec 通过Mocked注解mock对象
 */
@RunWith(JMockit.class)
public class BookServiceTest_Mocked {


    @Mocked
    BookService bookService;

    @Test
    public void testGetLibraryInfo() throws Exception {
        // 录制，定义被模拟的方法的返回值，与被调用的次数
        new Expectations() {{
            bookService.getLibraryInfo();
            result = "hello library";
            times = 2;
        }};

        // 回放，调用被模拟的方法
        System.out.println(bookService.getLibraryInfo());

        //这里new出来的BookService是Mock的对象
        System.out.println(new BookService().getLibraryInfo());

        System.out.println(bookService.displayBook(new Book()));


        // 验证，验证getLibraryInfo被调用，且被调用了两次
        new Verifications() {{
            bookService.getLibraryInfo();
            times = 2;
        }};
    }
}