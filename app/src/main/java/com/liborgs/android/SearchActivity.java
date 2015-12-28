package com.liborgs.android;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView mBack, localImage, webImage, cancel;
    private LinearLayout localLayout, webLayout, searchFilterLayout;
    private TextView localText, webText;
    private EditText myEditText;
    private List<HomeView> searchBooks;
    private RecyclerView recList;
    private SearchAdapter bookList;
    private static int selectedId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        searchFilterLayout = (LinearLayout) findViewById(R.id.searchFilterLayout);
        mBack = (ImageView) findViewById(R.id.searchBack);
        localImage = (ImageView) findViewById(R.id.local_search_image);
        webImage = (ImageView) findViewById(R.id.web_search_image);
        localLayout = (LinearLayout) findViewById(R.id.local_search_layout);
        webLayout = (LinearLayout) findViewById(R.id.web_search_layout);
        localText = (TextView) findViewById(R.id.local_search_text);
        webText = (TextView) findViewById(R.id.web_search_text);
        myEditText = (EditText) findViewById(R.id.myEditText);
        cancel = (ImageView) findViewById(R.id.cancel);
        recList = (RecyclerView) findViewById(R.id.searchResults);
        recList.setHasFixedSize(true);
        recList.setLayoutManager(new GridLayoutManager(this, 3));

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
                            recList.setAdapter(null);
                            searchFilterLayout.setVisibility(View.VISIBLE);
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
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    searchFilterLayout.setVisibility(View.GONE);
                    if (selectedId == 1)
                        fetchDataNormalSearch(myEditText.getText().toString());
                    else
                        fetchDataGoogleSearch(myEditText.getText().toString());
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
                selectedId = 1;
                break;

            case R.id.web_search_layout:
                webLayout.setBackgroundColor(Color.parseColor("#4CAF50"));
                webImage.setImageResource(R.drawable.ic_web_search_selected);
                webText.setTextColor(Color.parseColor("#FFFFFF"));
                localLayout.setBackgroundColor(Color.parseColor("#EEEEEE"));
                localImage.setImageResource(R.drawable.ic_local);
                localText.setTextColor(Color.parseColor("#000000"));
                selectedId = 2;
                break;

            case R.id.searchBack:
                onBackPressed();
                break;
        }
    }

    private void fetchDataNormalSearch(String query){
        String url = Uri.parse("https://liborgs-1139.appspot.com/books/search")
                .buildUpon()
                .appendQueryParameter("query", query)
                .build().toString();

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Searching");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();

        JsonObjectRequest jObject = new JsonObjectRequest(Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.hide();
                        try {
                            boolean status = response.getBoolean("status");
                            if(status)
                                extractSearchResponse(response);
                            else {
                                String message = response.getString("message");
                                new AlertDialog.Builder(SearchActivity.this)
                                        .setTitle("Liborg")
                                        .setMessage(message)
                                        .setPositiveButton(android.R.string.yes,
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                })
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.hide();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jObject);
    }

    private void fetchDataGoogleSearch(String query){
        String url = Uri.parse("https://liborgs-1139.appspot.com/books/search_google")
                .buildUpon()
                .appendQueryParameter("query", query)
                .build().toString();
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Searching");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();

        JsonObjectRequest jObject = new JsonObjectRequest(Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.hide();
                        try {
                            boolean status = response.getBoolean("status");
                            if(status)
                                extractSearchResponseGoogle(response);
                            else {
                                String message = response.getString("message");
                                new AlertDialog.Builder(SearchActivity.this)
                                        .setTitle("Liborg")
                                        .setMessage(message)
                                        .setPositiveButton(android.R.string.yes,
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                })
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.hide();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jObject);
    }

    private void extractSearchResponseGoogle(JSONObject response){
        searchBooks = new ArrayList<>();
        try {
            JSONArray data = response.getJSONArray("data");
            for (int i = 0; i < data.length(); i++){
                JSONObject book = (JSONObject) data.get(i);
                JSONArray authors = book.getJSONArray("authors");
                String authorName = "";
                for(int j = 0; j < authors.length() - 1; j++) {
                    authorName += (authors.get(j)) + ", ";
                }
                String publisher = book.getString("publisher");
                String title = book.getString("title");
                String thumbnail = book.getString("thumbnail");
                searchBooks.add(new HomeView(title, authorName, thumbnail, publisher));
            }
            bookList = new SearchAdapter(searchBooks, this);
            recList.setAdapter(bookList);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void extractSearchResponse(JSONObject response){
        searchBooks = new ArrayList<>();
        try {
            JSONArray data = response.getJSONArray("data");
            for(int i = 0; i < data.length(); i++){
                JSONObject book = (JSONObject) data.get(i);
                JSONArray authors = book.getJSONArray("author");
                JSONArray categories = book.getJSONArray("categories");
                String thumbnail = book.getString("thumbnail");
                String title = book.getString("title");
                int available = book.getInt("available");
                String description = book.getString("description");
                String pageCount = book.getString("pagecount");
                String publisher = book.getString("publisher");
                String authorName = "";
                for(int j = 0; j < authors.length() - 1; j++) {
                    authorName += (authors.get(j)) + ", ";
                }
                authorName += authors.get(authors.length()-1);
                String category = "NA";
                if(categories.length() != 0){
                    category = (String)categories.get(0);
                }
                searchBooks.add(new HomeView(title, authorName, thumbnail
                        , available, pageCount, description, publisher, category));
            }
            Log.e("SearchActivity", "Searchbooks size: "+searchBooks.size());
            bookList = new SearchAdapter(searchBooks, this);
            recList.setAdapter(bookList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
