package org.wso2.carbon.book.provider;


import org.wso2.carbon.book.provider.model.Book;

import java.util.List;

public interface BookProvider {
    void createBook(String bookName, String authorName, String ISBN);
    
    List<Book> listBooks();
}