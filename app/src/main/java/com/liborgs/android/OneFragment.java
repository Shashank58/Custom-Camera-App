package com.liborgs.android;

/**
 This is the home fragment of app which contains list of all books in the library.
 Fetches data of all books from server. Search of books is also handled here.
 **/

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout.LayoutParams;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.liborgs.android.register.LogInActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OneFragment extends Fragment {
    private RecyclerView recList;
    private Context mContext;
    private ImageView search;
    private Toolbar toolbar;
    private TextView fetchData;
    private List<HomeView> libraryBooks;
    private List<HomeView> listOfAllBooks;
    private HomeAdapter bookList;

    public OneFragment(){

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(isNetworkAvailable()) {
            getData();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_one, container, false);
        fetchData = (TextView) view.findViewById(R.id.fetching_data);
        if(isNetworkAvailable()) {
            searchFeature();
            recList = (RecyclerView) view.findViewById(R.id.cardList);
            recList.setHasFixedSize(true);
            mContext = getActivity().getBaseContext();
            LinearLayoutManager llm = new LinearLayoutManager(mContext);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recList.setLayoutManager(llm);
        } else {
            //Not connected to net
            fetchData.setText("Please connect to internet");
            fetchData.setVisibility(View.VISIBLE);
            LayoutParams params = (LayoutParams) fetchData.getLayoutParams();
            params.setMargins(80,140,0,0);
            fetchData.setLayoutParams(params);
        }

        return view;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void searchFeature(){
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setCollapsible(true);

        search = (ImageView) toolbar.findViewById(R.id.search_action);

        search.setOnClickListener(new OnClickListener() {

            @TargetApi(VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    private void fetchData(String query){
        String url = Uri.parse("https://liborgs-1139.appspot.com/books/search")
                        .buildUpon()
                        .appendQueryParameter("query", query)
                        .build().toString();

        final ProgressDialog dialog = new ProgressDialog(getActivity());
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
                                extractResponse(response);
                            else {
                                String message = response.getString("message");
                                new AlertDialog.Builder(getActivity())
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
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(jObject);
    }

    private void extractResponse(JSONObject response){
        libraryBooks = new ArrayList<>();
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
                libraryBooks.add(new HomeView(title, authorName, thumbnail
                        , available, pageCount, description, publisher, category));
            }
            bookList = new HomeAdapter(libraryBooks, mContext, getActivity());
            recList.setAdapter(bookList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setOriginalAdapter(){
        bookList = new HomeAdapter(listOfAllBooks, mContext, getActivity());
        recList.setAdapter(bookList);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void getData(){
        String url = "https://liborgs-1139.appspot.com/books/get_all_books";
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Fetching");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();

        JsonObjectRequest jRequest = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("One fragment", "Response: "+response.toString());
                dialog.hide();
                try {
                    boolean status = response.getBoolean("status");
                    if (status){
                        if (response.has("data")) {
                            extractResponse(response);
                            listOfAllBooks = new ArrayList<>(libraryBooks);
                        } else {
                            Log.e("One fragment", "No data");
                            fetchData.setText("Could not fetch data, check your internet connection");
                            fetchData.setVisibility(View.VISIBLE);
                            LayoutParams params = (LayoutParams) fetchData.getLayoutParams();
                            params.setMargins(80,140,0,0);
                            fetchData.setLayoutParams(params);
                        }
                    } else {
                        String message = response.getString("message");
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Liborg")
                                .setMessage(message)
                                .setPositiveButton(android.R.string.yes,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                SharedPreferencesHandler s = new SharedPreferencesHandler();
                                                s.deleteSharedPreference(getActivity());
                                                Intent intent = new Intent(getActivity(), LogInActivity.class);
                                                startActivity(intent);
                                                getActivity().finish();
                                            }
                                        }).show();
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
        }){
            public Map<String, String> getHeaders() throws AuthFailureError {
                SharedPreferencesHandler s = new SharedPreferencesHandler();
                String loggedIn = s.getKeyAuth(getActivity());
                HashMap<String, String> params = new HashMap<>();
                params.put("auth-token", loggedIn);
                return params;
            }
        };
        queue.add(jRequest);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }
}