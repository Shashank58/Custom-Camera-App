package com.liborgs.android;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView mBack, localImage, webImage, cancel;
    private LinearLayout localLayout, webLayout;
    private TextView localText, webText;
    private EditText myEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        mBack = (ImageView) findViewById(R.id.searchBack);
        localImage = (ImageView) findViewById(R.id.local_search_image);
        webImage = (ImageView) findViewById(R.id.web_search_image);
        localLayout = (LinearLayout) findViewById(R.id.local_search_layout);
        webLayout = (LinearLayout) findViewById(R.id.web_search_layout);
        localText = (TextView) findViewById(R.id.local_search_text);
        webText = (TextView) findViewById(R.id.web_search_text);
        myEditText = (EditText) findViewById(R.id.myEditText);
        cancel = (ImageView) findViewById(R.id.cancel);

        localLayout.setOnClickListener(this);
        webLayout.setOnClickListener(this);
        mBack.setOnClickListener(this);

        editTextListeners();
    }

    private void editTextListeners(){
        myEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0) {
                    cancel.setImageResource(R.drawable.ic_speech);
                } else {
                    cancel.setImageResource(R.drawable.ic_cancel);
                    cancel.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myEditText.setText("");
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        myEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Log.e("Search activity", "Searching...");
                    handled = true;
                }

                return handled;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.local_search_layout:
                localLayout.setBackgroundColor(Color.parseColor("#4CAF50"));
                localImage.setImageResource(R.drawable.ic_local_selected);
                localText.setTextColor(Color.parseColor("#FFFFFF"));
                webLayout.setBackgroundColor(Color.parseColor("#EEEEEE"));
                webImage.setImageResource(R.drawable.ic_web_search);
                webText.setTextColor(Color.parseColor("#000000"));
                break;

            case R.id.web_search_layout:
                webLayout.setBackgroundColor(Color.parseColor("#4CAF50"));
                webImage.setImageResource(R.drawable.ic_web_search_selected);
                webText.setTextColor(Color.parseColor("#FFFFFF"));
                localLayout.setBackgroundColor(Color.parseColor("#EEEEEE"));
                localImage.setImageResource(R.drawable.ic_local);
                localText.setTextColor(Color.parseColor("#000000"));
                break;

            case R.id.searchBack:
                onBackPressed();
                break;
        }
    }
}
