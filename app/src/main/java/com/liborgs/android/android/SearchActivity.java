package com.liborgs.android.android;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.liborgs.android.R;
import com.liborgs.android.adapters.SearchAdapter;
import com.liborgs.android.datamodle.HomeView;
import com.liborgs.android.util.AppUtils;
import com.liborgs.android.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {
    private ImageView mBack, cancel;
    private EditText myEditText;
    private List<Object> searchBooks;
    private RecyclerView recList;
    private SearchAdapter bookList;
    private static int size = 0; //Changing grid size to occupy full space for Section headers

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

        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak Now");
        try {
            startActivityForResult(intent, Constants.REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Not supported",
                    Toast.LENGTH_SHORT).show();
        }
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
                    cancel.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            promptSpeechInput();
                        }
                    });
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    myEditText.setText(result.get(0));
                }
                break;
            }

        }
    }

    private void fetchDataNormalSearch(String query){
        searchBooks = new ArrayList<>();
        String url = Uri.parse(Constants.LOCAL_BOOK_SEARCH)
                .buildUpon()
                .appendQueryParameter("query", query)
                .build().toString();

        AppUtils.getInstance().showProgressDialog(this, "Searching");

        JsonObjectRequest jObject = new JsonObjectRequest(Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        AppUtils.getInstance().dismissProgressDialog();
                        try {
                            boolean status = response.getBoolean("status");
                            if(status) {
                                searchBooks.add("Local Results");
                                extractSearchResponse(response, true);
                                fetchDataGoogleSearch(myEditText.getText().toString());
                                size = searchBooks.size() - 1;
                            }
                            else {
                                size = 0;
                                fetchDataGoogleSearch(myEditText.getText().toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AppUtils.getInstance().dismissProgressDialog();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jObject);
    }

    private void fetchDataGoogleSearch(String query){
        searchBooks.add("Web Results");
        String url = Uri.parse(Constants.GOOGLE_BOOK_SEARCH)
                .buildUpon()
                .appendQueryParameter("query", query)
                .build().toString();
        AppUtils.getInstance().showProgressDialog(this, "Searching");

        JsonObjectRequest jObject = new JsonObjectRequest(Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        AppUtils.getInstance().dismissProgressDialog();
                        try {
                            boolean status = response.getBoolean("status");
                            if(status)
                                extractSearchResponse(response, false);
                            else {
                                String message = response.getString("message");
                                AppUtils.getInstance().alertMessage(SearchActivity.this,
                                        Constants.LIBORGS, message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AppUtils.getInstance().dismissProgressDialog();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jObject);
    }


    private void extractSearchResponse(JSONObject response, boolean isBookLocal){
        try {
            JSONArray data = response.getJSONArray("data");
            for(int i = 0; i < data.length(); i++){
                JSONObject book = (JSONObject) data.get(i);
                JSONArray authors = book.getJSONArray("author");
                JSONArray categories = book.getJSONArray("categories");
                String thumbnail = book.getString("thumbnail");
                String title = book.getString("title");
                int available = -1;
                if (book.has("available"))
                    available = book.getInt("available");
                String description = book.getString("description");
                String pageCount = book.getString("pagecount");
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
                        , webReaderLink, isBookLocal));
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
