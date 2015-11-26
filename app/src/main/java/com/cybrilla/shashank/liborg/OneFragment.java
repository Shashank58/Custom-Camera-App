package com.cybrilla.shashank.liborg;

/**
 * Created by shashankm on 16/11/15.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class OneFragment extends Fragment {
    private HomeAdapter bookList;
    private List<HomeView> libraryBooks;
    private List<HomeView> listOfAllBooks;
    private RecyclerView recList;

    public OneFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        getData();


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
        bookList = new HomeAdapter(libraryBooks, getActivity().getBaseContext());

        recList.setAdapter(bookList);
    }

    private void getData(){
        libraryBooks = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getActivity().getBaseContext().getAssets().open("library.txt")));

            // do reading, usually loop until end of file reading
            String mLine;
            String completeText = "";
            while ((mLine = reader.readLine()) != null) {
                //process line
                completeText += mLine;
            }
            try {
                JSONObject jObject = new JSONObject(completeText);
                JSONArray books = jObject.getJSONArray("books");
                for(int i = 0; i < books.length(); i++){
                    JSONObject book = (JSONObject) books.get(i);
                    String name = book.getString("name");
                    String author = book.getString("author");
                    libraryBooks.add(new HomeView(name, author));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

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
                final List<HomeView> filteredModelList = filter(libraryBooks, newText);
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