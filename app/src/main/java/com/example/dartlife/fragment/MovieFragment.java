package com.example.dartlife.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.dartlife.R;
import com.example.dartlife.adapter.CustomAdapter;

public class MovieFragment extends Fragment {
    CustomAdapter customAdapter;
    RecyclerView list;
    public MovieFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_movie, container, false);
        list = v.findViewById(R.id.movie_view);
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        customAdapter = new CustomAdapter(this.getActivity());

        list.setAdapter(customAdapter);

        return v;

    }

}
