package com.cybrilla.shashank.liborg;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.util.List;

/**
 * Created by shashankm on 24/11/15.
 */
public class SearchActivity extends AppCompatActivity {
    private EditText mSearchEt;
    private List<HomeView> mBookFilter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_bar);

        mSearchEt = (EditText) findViewById(R.id.etSearch);
    }

    public void searchBooks(View v){
        String text = mSearchEt.getText().toString();
    }
}