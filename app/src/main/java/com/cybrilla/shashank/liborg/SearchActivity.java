package com.cybrilla.shashank.liborg;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by shashankm on 24/11/15.
 */
public class SearchActivity extends AppCompatActivity {
    private EditText mSearchEt;
    private ImageView searchText;

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
        searchText = (ImageView) findViewById(R.id.search_text);

        searchText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mSearchEt.getText().toString();
            }
        });
    }

}