package com.cybrilla.shashank.liborg;

/**
 * Created by shashankm on 16/11/15.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

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
    private static final String LIB_KEY = "Liborg Auth";
    private static final int PRIVATE_MODE = 0;
    private static final String KEY_AUTH = "auth_key" ;
    private HomeAdapter bookList;
    private List<HomeView> libraryBooks;
    private List<HomeView> listOfAllBooks;

    public OneFragment(){

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_one, container, false);
        recList = (RecyclerView) view.findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        mContext = getActivity().getBaseContext();
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void getData(){
        libraryBooks = new ArrayList<>();
        String url = "https://liborgs-1139.appspot.com/books/get_all_books";
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jRequest = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
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
                        String authorName = (String) authors.get(0);
                        String category = "NA";
                        if(categories.length() != 0){
                            category = (String)categories.get(0);
                        }
                        libraryBooks.add(new HomeView(title, authorName, thumbnail
                                , available, pageCount, description, publisher, category));
                    }
                    listOfAllBooks = new ArrayList<>(libraryBooks);

                    bookList = new HomeAdapter(libraryBooks, mContext, getActivity());

                    recList.setAdapter(bookList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            public Map<String, String> getHeaders() throws AuthFailureError {
                SharedPreferences pref = getActivity().getSharedPreferences(LIB_KEY, PRIVATE_MODE);
                String loggedIn = pref.getString(KEY_AUTH, null);
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

//        MenuItem item = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
//
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                Log.e("One fragment", "This");
//                Toast.makeText(getActivity(), "Our word : " + s, Toast.LENGTH_SHORT).show();
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//                return false;
//            }
//        });
    }
}