package com.cybrilla.shashank.liborg;

/**
 * Created by shashankm on 17/11/15.
 */
public class HomeView {
    private String bookName;
    private String authorName;
    private String dueDate;
    private String thumbnail;

    public HomeView(String bookName, String authorName, String thumbnail){
        super();
        this.bookName = bookName;
        this.authorName = authorName;
        this.thumbnail = thumbnail;
    }

    public HomeView(String bookName, String authorName, String thumbnail,String dueDate){
        super();
        this.bookName = bookName;
        this.authorName = authorName;
        this.dueDate = dueDate;
        this.thumbnail = thumbnail;
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
}
