package com.liborgs.android;

import java.io.Serializable;

/**
 * Class for storing and getting all the book details.
 */
public class HomeView implements Serializable {
    private String bookName;
    private String authorName;
    private String dueDate;
    private String borrowedDate;
    private String thumbnail;
    private long issueDate;
    private int available;
    private String pageCount;
    private String description;
    private String publisher;
    private String categories;
    private String isbn;
    private String returnDate;
    private int returnStatus;

    public HomeView(String bookName, String authorName, String thumbnail
            , int available, String pageCount, String description, String publisher, String categories){
        super();
        this.bookName = bookName;
        this.authorName = authorName;
        this.thumbnail = thumbnail;
        this.available = available;
        this.pageCount = pageCount;
        this.description = description;
        this.publisher = publisher;
        this.categories = categories;
    }

    public HomeView(String bookName, String authorName, String borrowedDate, String thumbnail
            , String dueDate, long issueDate, String isbn, String returnDate, int returnStatus){
        this.bookName = bookName;
        this.authorName = authorName;
        this.dueDate = dueDate;
        this.thumbnail = thumbnail;
        this.borrowedDate = borrowedDate;
        this.issueDate = issueDate;
        this.isbn = isbn;
        this.returnDate = returnDate;
        this.returnStatus = returnStatus;
    }

    public HomeView(String bookName, String authorName, String thumbnail, String publisher) {
        this.bookName = bookName;
        this.authorName = authorName;
        this.thumbnail = thumbnail;
        this.publisher = publisher;
    }

    public String getBookName(){
        return bookName;
    }

    public String getAuthorName(){
        return authorName;
    }

    public String getDueDate(){
        return dueDate;
    }

    public String getThumbnail(){
        return thumbnail;
    }

    public int getAvailable(){
        return available;
    }

    public String getPageCount(){
        return pageCount;
    }

    public String getDescription(){
        return description;
    }

    public String getPublisher(){
        return publisher;
    }

    public String getCategories(){
        return categories;
    }

    public String getBorrowedDate(){
        return borrowedDate;
    }

    public long getIssueDate(){
        return issueDate;
    }

    public String getIsbn(){
        return isbn;
    }

    public String getReturnDate(){
        return returnDate;
    }

    public int getReturnStatus(){
        return returnStatus;
    }
}
