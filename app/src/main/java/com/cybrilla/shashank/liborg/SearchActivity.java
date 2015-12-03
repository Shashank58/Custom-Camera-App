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
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        // Never show the action bar if the
        // Status bar is hidden, so hide that too if necessary.
        getSupportActionBar().hide();

        mSearchEt = (EditText) findViewById(R.id.etSearch);
    }

    public void searchBooks(View v){
        String text = mSearchEt.getText().toString();
    }
}