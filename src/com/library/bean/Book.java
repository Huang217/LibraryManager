package com.library.bean;

import java.io.InputStream;

public class Book {
    private int bookId;
    private String isbn;
    private String title;
    private String author;
    private String press;
    private String category;
    private InputStream cover; // 数据库大对象
    private int total;
    private int remain;
    private int status;

    public Book() {}
    // getters and setters
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getPress() { return press; }
    public void setPress(String press) { this.press = press; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public InputStream getCover() { return cover; }
    public void setCover(InputStream cover) { this.cover = cover; }
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
    public int getRemain() { return remain; }
    public void setRemain(int remain) { this.remain = remain; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
}