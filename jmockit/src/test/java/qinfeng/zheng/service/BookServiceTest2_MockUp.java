package qinfeng.zheng.service;

import mockit.*;
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
public class BookServiceTest2_MockUp {

    @Test
    public void testGetLibraryInfo() throws Exception {
        new MockUp<BookService>(BookService.class) {
            @Mock
            public String getLibraryInfo() {
                return "mock data!";
            }

//            @Mock
//            public String displayBook(Book book) {
//                return "我只想呵呵";
//            }
        };

        // 回放，调用了模拟方法
        System.out.println(new BookService().getLibraryInfo());
        System.out.println(new BookService().getLibraryInfo());

        // 如果模拟方法中没有displayBook方法，这里就会调用真实方法， 有模拟方法就会调用模拟方法
        System.out.println(new BookService().displayBook(new Book()));

    }

}