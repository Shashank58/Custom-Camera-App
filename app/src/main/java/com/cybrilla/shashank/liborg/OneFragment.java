package com.cybrilla.shashank.liborg;

/**
 * Created by shashankm on 16/11/15.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class OneFragment extends Fragment {
    HomeAdapter bookList;
    List<HomeView> libraryBooks;
    Context mcontext;

    public OneFragment(Context context) {
        // Required empty public constructor
        mcontext = context;
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
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(mcontext.getAssets().open("library.txt")));

            // do reading, usually loop until end of file reading
            String mLine;
            String completeText = "";
            while ((mLine = reader.readLine()) != null) {
                //process line
               completeText += mLine;
            }
            try {
                JSONObject jObject = new JSONObject(completeText);
                for(int i = 0; i < jObject.length(); i++){
                    libraryBooks.add(new HomeView(jObject.getString("100"+i)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }

        bookList = new HomeAdapter(libraryBooks);
        recList.setAdapter(bookList);

        return view;
    }

}
