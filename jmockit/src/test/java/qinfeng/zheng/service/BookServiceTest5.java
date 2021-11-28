package qinfeng.zheng.service;

import mockit.Expectations;
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
 * @dec  局部模拟， 需要注意的时这里bookService需要初始化，并且不能加@Mocked或@Injectable注解了。
 */
@RunWith(JMockit.class)
public class BookServiceTest5 {

    BookService bookService = new BookService();

    @Test
    public void testGetBook() throws Exception {
        final Book book = new Book();
        book.setName("Harry Porter");

        new Expectations(BookService.class){{
            // 只模拟了getBook方法
            bookService.getBook(anyString);
//            bookService.getBook("123");
            result = book;
        }};
        // 因为前面记录了getBook方法，所以这里会走mock的getBook方法
        System.out.println(bookService.getBook("123").getName());

        // 因为前面没有记录getLibraryInfo方法，所以这里会走真实的方法
        System.out.println(bookService.getLibraryInfo());
    }
}