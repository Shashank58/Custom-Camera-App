package com.liborgs.android;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
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

public class SearchActivity extends AppCompatActivity {
    private ImageView mBack, cancel;
    private EditText myEditText;
    private List<Object> searchBooks;
    private RecyclerView recList;
    private SearchAdapter bookList;
    private static int size = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        mBack = (ImageView) findViewById(R.id.searchBack);
        myEditText = (EditText) findViewById(R.id.myEditText);
        cancel = (ImageView) findViewById(R.id.cancel);
        recList = (RecyclerView) findViewById(R.id.searchResults);
        recList.setHasFixedSize(true);

        editTextListeners();

        mBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
                    recList.setAdapter(null);
                    cancel.setImageResource(R.drawable.ic_cancel);
                    cancel.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myEditText.setText("");
                            recList.setAdapter(null);
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
                    fetchDataNormalSearch(myEditText.getText().toString());
                    handled = true;
                }

                return handled;
            }
        });
    }

    private void fetchDataNormalSearch(String query){
        searchBooks = new ArrayList<>();
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
                            if(status) {
                                searchBooks.add("Local Results");
                                extractSearchResponse(response);
                                fetchDataGoogleSearch(myEditText.getText().toString());
                                size = searchBooks.size() - 1;
                            }
                            else {
                                String message = response.getString("message");
                                new AlertDialog.Builder(SearchActivity.this)
                                        .setTitle("Liborg")
                                        .setMessage(message)
                                        .setPositiveButton(android.R.string.yes,
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        size = 0;
                                                        fetchDataGoogleSearch(myEditText.getText().toString());
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
        searchBooks.add("Web Results");
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


    private void extractSearchResponse(JSONObject response){
        try {
            JSONArray data = response.getJSONArray("data");
            for(int i = 0; i < data.length(); i++){
                JSONObject book = (JSONObject) data.get(i);
                JSONArray authors = null;
                if (book.has("author"))
                    authors = book.getJSONArray("author");
                else
                    authors = book.getJSONArray("authors");
                JSONArray categories = book.getJSONArray("categories");
                String thumbnail = book.getString("thumbnail");
                String title = book.getString("title");
                int available = -1;
                if (book.has("available"))
                    available = book.getInt("available");
                String description = book.getString("description");
                String pageCount = "NA";
                if (book.has("pagecount"))
                    pageCount = book.getString("pagecount");
                else
                    pageCount = book.getString("pageCount");
                String publisher = book.getString("publisher");
                String authorName = "";
                if (authors.length() > 0) {
                    for (int j = 0; j < authors.length() - 1; j++) {
                        authorName += (authors.get(j)) + ", ";
                    }
                    authorName += authors.get(authors.length() - 1);
                }
                String category = "NA";
                if(categories.length() != 0){
                    category = (String)categories.get(0);
                }
                String averageRating = "NA";
                if (book.has("averageRating"))
                    averageRating = book.getString("averageRating");
                String webReaderLink = "NA";
                if (book.has("webReaderLink"))
                    webReaderLink = book.getString("webReaderLink");

                searchBooks.add(new HomeView(title, authorName, thumbnail
                        , available, pageCount, description, publisher, category, averageRating
                        , webReaderLink));
            }
            bookList = new SearchAdapter(searchBooks, this);
            GridLayoutManager manager = new GridLayoutManager(this, 3);
            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (position == 0 || position == size) {
                        return 3;
                    }
                    else
                        return 1;
                }
            });
            recList.setLayoutManager(manager);
            recList.setAdapter(bookList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
