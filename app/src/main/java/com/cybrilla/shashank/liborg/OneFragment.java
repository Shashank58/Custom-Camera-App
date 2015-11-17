package com.cybrilla.shashank.liborg;

/**
 * Created by shashankm on 16/11/15.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class OneFragment extends Fragment {
    HomeAdapter bookList;
    List<HomeView> libraryBooks;

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
        RecyclerView recList = (RecyclerView) view.findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity().getBaseContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        libraryBooks = new ArrayList<>();
        libraryBooks.add(new HomeView("Effective Java"));
        libraryBooks.add(new HomeView("Game Of Thrones"));
        libraryBooks.add(new HomeView("Ineffective Java"));
        libraryBooks.add(new HomeView("Mildly Effective Java"));
        libraryBooks.add(new HomeView("Java...?"));
        bookList = new HomeAdapter(libraryBooks);
        recList.setAdapter(bookList);

        return view;
    }

}
