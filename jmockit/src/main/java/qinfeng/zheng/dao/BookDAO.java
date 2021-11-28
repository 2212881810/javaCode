package qinfeng.zheng.dao;

import qinfeng.zheng.entry.Book;

/**
 * @Author ZhengQinfeng
 * @Date 2021/3/31 23:09
 * @dec
 */
public class BookDAO {
    public Book getBookById(String id) {
        Book book = new Book();
        book.setId(id);
        book.setName("Java");
        return book;
    }

    public void save(Book book) {

    }

    public Book findById(String id) {
        return null;
    }
}
