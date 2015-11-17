package com.cybrilla.shashank.liborg;

/**
 * Created by shashankm on 17/11/15.
 */
public class HomeView {
    private String bookName;
    private String authorName;
    private String dueDate;

    public HomeView(String bookName, String authorName){
        super();
        this.bookName = bookName;
        this.authorName = authorName;
    }

    public HomeView(String bookName, String authorName, String dueDate){
        super();
        this.bookName = bookName;
        this.authorName = authorName;
        this.dueDate = dueDate;
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
}
