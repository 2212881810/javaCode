package qinfeng.zheng.service;

import qinfeng.zheng.dao.BookDAO;
import qinfeng.zheng.entry.Book;

public class BookService {
    private static String LIB_NAME = "国家图书馆";
    private String serName;

    BookDAO bookDAO;

    public void setBookDAO(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    public void saveBook(Book book) {
        bookDAO.save(book);
    }

    public Book getBook(String id) {
    /*    Book book = new Book();
        book.setName("admin");

        return book;*/
                return bookDAO.findById(id);

    }

    public String displayBook(Book book) {
        String res = String.format("name: %s\nauthor:%s\npubilsher:%s", book.getName(), book.getAuthor(), book.getPubilisher());
        return res;
    }

    public String getLibraryInfo() {
        return "Welcome to library!";
    }

    private String getSerName() {
        return serName;
    }

    private static String getLibName() {
        return LIB_NAME;
    }
}