package org.wso2.carbon.book.reader;

import org.wso2.carbon.book.reader.internal.ReaderDataHolder;

public class Reader {
    public void getBookCreated(String bookName, String authorName,String ISBN){
        ReaderDataHolder.getInstance().getBookProvider().createBook(bookName, authorName, ISBN);
    }
}