package qinfeng.zheng.service;

import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import qinfeng.zheng.dao.BookDAO;
import qinfeng.zheng.entry.Book;

import java.lang.reflect.Method;

/**
 * @Author ZhengQinfeng
 * @Date 2021/3/31 23:12
 * @dec  操作私有属性
 */
@RunWith(JMockit.class)
public class BookServiceTest7_field {


    private BookService bookService = new BookService();

    @Test
    public void testGetSerName() throws Exception{
        new Expectations(BookService.class) {{
            Deencapsulation.setField(bookService, "serName", "蜀山");
        }};

        Method method = bookService.getClass().getDeclaredMethod("getSerName");
        method.setAccessible(true);
        String res = (String) method.invoke(bookService);

        Assert.assertEquals(res, "蜀山");
    }

}