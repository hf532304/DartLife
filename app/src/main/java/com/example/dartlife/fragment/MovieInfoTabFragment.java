package com.example.dartlife.fragment;

import android.graphics.Movie;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dartlife.R;
import com.example.dartlife.model.MovieBookEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;


public class MovieInfoTabFragment extends Fragment {
    TextView mMovieDescriptionTextView;
    TextView mMoviePlaytime;
    String mMovieDescription;
    String mMovieId;
    private static final String MOVIE_DESCRIPTION = "descriptionKey";
    private static final String MOVIE_ID = "idKey";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;

    MovieBookEntry mMovie;

    public MovieInfoTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.movie_info_tab_fragment, container, false);
        mMovieDescriptionTextView = v.findViewById(R.id.MovieDescription);
        mMoviePlaytime = v.findViewById(R.id.MoviePlaytime);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mMovieDescription = getActivity().getIntent().getStringExtra(MOVIE_DESCRIPTION);
        mMovieId = getActivity().getIntent().getStringExtra(MOVIE_ID);

        mMovieDescriptionTextView.setText(mMovieDescription);

        mDatabase.child("movies").child(mMovieId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMovie = dataSnapshot.getValue(MovieBookEntry.class);
                mMoviePlaytime.setText(mMovie.getPlayTime());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return v;
    }


}
