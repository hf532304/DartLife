package com.example.dartlife.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dartlife.R;


public class MovieInfoTabFragment extends Fragment {
    TextView mMovieDescriptionTextView;
    String mMovieDescription;
    private static final String MOVIE_DESCRIPTION = "descriptionKey";

    public MovieInfoTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.movie_info_tab_fragment, container, false);
        mMovieDescriptionTextView = v.findViewById(R.id.MovieDescription);

        mMovieDescription = getActivity().getIntent().getStringExtra(MOVIE_DESCRIPTION);

        mMovieDescriptionTextView.setText(mMovieDescription);
        return v;
    }


}
