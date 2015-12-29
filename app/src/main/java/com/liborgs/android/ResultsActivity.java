package com.liborgs.android;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {
    private RecyclerView resultList;
    private List<HomeView> resultBooks;
    private ResultsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#009688")));
        }
        resultList = (RecyclerView) findViewById(R.id.resultList);
        resultList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        resultList.setLayoutManager(llm);
        String result = getIntent().getStringExtra("response");
        JSONObject response = null;
        boolean status = false;
        try {
            response = new JSONObject(result);
            status = response.getBoolean("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(status)
            showResults(response);
        else {
            try {
                String message = response.getString("message");
                new AlertDialog.Builder(ResultsActivity.this)
                                .setTitle("Liborg")
                                .setMessage(message)
                                .setPositiveButton(android.R.string.yes,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        })
                                .show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void showResults(JSONObject response){
        resultBooks = new ArrayList<>();
        try {
            JSONArray data = response.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
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
                for (int j = 0; j < authors.length() - 1; j++) {
                    authorName += (authors.get(j)) + ", ";
                }
                authorName += authors.get(authors.length() - 1);
                String category = "NA";
                if (categories.length() != 0) {
                    category = (String) categories.get(0);
                }
                String averageRaing = "NA";
                if (book.has("averageRating"))
                    averageRaing = book.getString("averageRating");
                String webReaderLink = "NA";
                if (book.has("webReaderLink"))
                    webReaderLink = book.getString("webReaderLink");
                resultBooks.add(new HomeView(title, authorName, thumbnail
                        , available, pageCount, description, publisher, category
                        , averageRaing, webReaderLink));
            }

            adapter = new ResultsAdapter(resultBooks, this);
            resultList.setAdapter(adapter);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
}
