package qinfeng.zheng.service;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import qinfeng.zheng.dao.BookDAO;
import qinfeng.zheng.entry.Book;

/**
 * @Author ZhengQinfeng
 * @Date 2021/3/31 23:12
 * @dec 使用@MockUp + @Mock两个注解来模拟对象和特定的方法
 */
@RunWith(JMockit.class)
public class BookServiceTest4_Mock {
    @Test
    public void testGetBook() {
        // 通过getMockInstance方法构造了一个假的BookDAO实例
        BookDAO bookDAO = new MockUp<BookDAO>() {
            @Mock
            Book findById(String id) {
                Book book = new Book();
                book.setName("上下五千年");
                return book;
            }
        }.getMockInstance();


        BookService bookService = new BookService();
        bookService.setBookDAO(bookDAO);
        // bookService调用了模拟的方法
        Assert.assertEquals(bookService.getBook("123").getName(), "上下五千年");
    }
}