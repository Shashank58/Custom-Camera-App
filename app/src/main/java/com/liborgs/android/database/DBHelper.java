package com.liborgs.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.liborgs.android.datamodle.HomeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shashankm on 28/01/16.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "AllBooks.db";
    private static final int DATABAS_VERSION = 1;
    private static final String TABLE_NAME = "books";
    private static final String BOOK_ID = "book_id";
    private static final String BOOK_NAME = "book_name";
    private static final String AUTHOR_NAME = "author_name";
    private static final String THUMB_NAIL = "thumbnail";
    private static final String AVAILABLE = "available";
    private static final String PAGE_COUNT = "page_count";
    private static final String DESCRIPTION = "description";
    private static final String PUBLISHER = "publisher";
    private static final String CATEGORIES = "categories";
    private static final String AVERAGE_RATINGS = "average_ratings";
    private static final String WEB_READER_LINK = "web_reader_link";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABAS_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" + BOOK_ID +
                " INTEGER PRIMARY KEY, " + BOOK_NAME + " TEXT, " + AUTHOR_NAME +
                " TEXT, " + THUMB_NAIL + " TEXT, " + AVAILABLE + " INTEGER, " + PAGE_COUNT
                + " TEXT, " + DESCRIPTION + " TEXT, " + PUBLISHER + " TEXT, " + CATEGORIES
                + " TEXT, " + AVERAGE_RATINGS + " TEXT, " + WEB_READER_LINK + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public int getCount(){
        SQLiteDatabase db = this.getWritableDatabase();
        String countQuery = "SELECT * from " + TABLE_NAME;
        Cursor c = db.rawQuery(countQuery, null);
        c.moveToFirst();
        db.close();
        if (c.getCount() > 0) {
            return c.getInt(0);
        }
        else {
            c.close();
            return 0;
        }
    }

    public void insertAllBooks(List<HomeView> allBooks){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        for (HomeView book : allBooks) {
            contentValues.put(BOOK_NAME, book.getBookName());
            contentValues.put(AUTHOR_NAME, book.getAuthorName());
            contentValues.put(THUMB_NAIL, book.getThumbnail());
            contentValues.put(AVAILABLE, book.getAvailable());
            contentValues.put(PAGE_COUNT, book.getPageCount());
            contentValues.put(DESCRIPTION, book.getDescription());
            contentValues.put(PUBLISHER, book.getPublisher());
            contentValues.put(CATEGORIES, book.getCategories());
            contentValues.put(AVERAGE_RATINGS, book.getAverageRating());
            contentValues.put(WEB_READER_LINK, book.getWebReaderLink());
            db.insert(TABLE_NAME, null, contentValues);
            Log.e("DB Helper", "Inserting....");
        }
        db.close();
    }

    public List<HomeView> getAllBooks(){
        List<HomeView> allBooks = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (res.moveToFirst()){
            do {
                allBooks.add(new HomeView(res.getString(1), res.getString(2), res.getString(3)
                    , res.getInt(4), res.getString(5), res.getString(6), res.getString(7),
                        res.getString(8), res.getString(9), res.getString(10), true));
            } while (res.moveToNext());
        }
        db.close();
        res.close();
        Log.e("DBHelper", "Size of books: " + allBooks.size());
        return allBooks;
    }
}
