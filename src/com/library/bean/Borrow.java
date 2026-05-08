package com.library.bean;

import java.sql.Timestamp;

public class Borrow {
    private long borrowId;
    private int userId;
    private int bookId;
    private String username;    // 来自视图查询
    private String bookTitle;
    private String isbn;
    private Timestamp borrowTime;
    private Timestamp returnTime;
    private String state;

    public Borrow() {}
    // getters and setters
    public long getBorrowId() { return borrowId; }
    public void setBorrowId(long borrowId) { this.borrowId = borrowId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public Timestamp getBorrowTime() { return borrowTime; }
    public void setBorrowTime(Timestamp borrowTime) { this.borrowTime = borrowTime; }
    public Timestamp getReturnTime() { return returnTime; }
    public void setReturnTime(Timestamp returnTime) { this.returnTime = returnTime; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
}