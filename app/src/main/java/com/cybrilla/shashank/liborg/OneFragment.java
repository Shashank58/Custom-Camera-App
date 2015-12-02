package com.cybrilla.shashank.liborg;

/**
 * Created by shashankm on 16/11/15.
 */

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private HomeAdapter bookList;
    private List<HomeView> libraryBooks;
    private List<HomeView> listOfAllBooks;
    private RecyclerView recList;
    private static final String LIB_KEY = "Liborg Auth";
    private static final int PRIVATE_MODE = 0;
    private static final String KEY_AUTH = "auth_key" ;

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
        LinearLayoutManager llm = new LinearLayoutManager(getActivity().getBaseContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        Log.e("One Fragment","On create view");

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

//        recList.setLayoutManager(new LinearLayoutManager(getActivity()));
//
//        libraryBooks = new ArrayList<>();
//
////        for (String movie : MOVIES) {
////            libraryBooks.add(new HomeView(movie));
////        }
//
//        bookList = new HomeAdapter(libraryBooks, getActivity());
//        recList.setAdapter(bookList);

    }

    private void getData(){
        libraryBooks = new ArrayList<>();
        Log.e("One Fragment", "Getting called?");
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
                        String thumbnail = book.getString("thumbnail");
                        String title = book.getString("title");
                        libraryBooks.add(new HomeView(title, (String)authors.get(0), thumbnail));
                    }
                    Log.e("One Fragment", "Length of librarybooks: "+libraryBooks.size());
                    listOfAllBooks = new ArrayList<>(libraryBooks);
                    bookList = new HomeAdapter(libraryBooks, getActivity().getBaseContext());

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
        Log.e("One fragment", "Is this getting called?");
        queue.add(jRequest);
//        recList.setOnScrollListener(new HidingScrollListener() {
//            @Override
//            public void onHide() {
//                hideViews();
//            }
//
//            @Override
//            public void onShow() {
//                showViews();
//            }
//        });
    }

//    private void hideViews() {
//        mToolbar.animate().translationY(-mToolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));

//        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFabButton.getLayoutParams();
//        int fabBottomMargin = lp.bottomMargin;
//        mFabButton.animate().translationY(mFabButton.getHeight()+fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
//    }

 //   private void showViews() {
//        mToolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
//        mFabButton.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
//    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                final List<HomeView> filteredModelList = filter(listOfAllBooks, newText);
                bookList.animateTo(filteredModelList);
                recList.scrollToPosition(0);
                return true;
            }
        });
    }

    private List<HomeView> filter(List<HomeView> models, String query) {
        query = query.toLowerCase();

        final List<HomeView> filteredModelList = new ArrayList<>();
        for (HomeView model : models) {
            final String text = model.getBookName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

}