package qinfeng.zheng.service;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import qinfeng.zheng.entry.Book;

/**
 * @Author ZhengQinfeng
 * @Date 2021/3/31 23:12
 * @dec  使用@MockUp + @Mock两个注解来模拟对象和特定的方法
 */
@RunWith(JMockit.class)
public class BookServiceTest3_Injectable {

    @Injectable
    private BookService bookService;

    @Test
    public void testGetLibraryInfo() throws Exception {
        new Expectations() {{
            bookService.getLibraryInfo();
            result = "hello mock";
        }};

        // 调用了模拟对象的模拟方法
        System.out.println(bookService.getLibraryInfo());
        System.out.println(bookService.getLibraryInfo());

        // 调用了原生的方法
        System.out.println(new BookService().getLibraryInfo());

    }

}