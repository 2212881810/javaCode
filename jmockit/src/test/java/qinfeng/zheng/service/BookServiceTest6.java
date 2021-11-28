package qinfeng.zheng.service;

import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import qinfeng.zheng.dao.BookDAO;
import qinfeng.zheng.entry.Book;

/**
 * @Author ZhengQinfeng
 * @Date 2021/3/31 23:12
 * @dec
 */
@RunWith(JMockit.class)
public class BookServiceTest6 {

    private BookService bookService = new BookService();;

    @Injectable
    private BookDAO bookDAO;

    @Test
    public void testGetBook() {
        final Book book = new Book();
        book.setName("上下五千年");
        new Expectations(){{
            bookDAO.findById(anyString);
            result = book;
        }};


        final Book book1 = new Book();
        book1.setName("百年孤独");

        new MockUp<BookService>(BookService.class) {
            @Mock
            public Book getBook(Invocation invocation, String id){
                if ("abc".equals(id)) {
                    return book1;
                } else {
                    // 会调用了原生的方法，如果原生的方法被mock了，就会调用mock方法
                    return invocation.proceed(id);
                }
            }
        };

        bookService.setBookDAO(bookDAO);

        Assert.assertEquals(bookService.getBook("abc").getName(), "百年孤独");
        Assert.assertEquals(bookService.getBook("123").getName(), "上下五千年");
    }
}