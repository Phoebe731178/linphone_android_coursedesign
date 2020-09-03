package com.hunter.linphoneandroid.vo;

import com.hunter.android.vo.Page;

import java.util.List;

public class DoubanResponse extends Page {
    private List<Book> books;

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
