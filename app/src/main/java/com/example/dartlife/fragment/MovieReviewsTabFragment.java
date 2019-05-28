package com.example.dartlife.fragment;

import android.graphics.Movie;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.dartlife.R;
import com.example.dartlife.activity.MovieInfoActivity;
import com.example.dartlife.adapter.ReviewAdapter;
import com.example.dartlife.model.MovieBookEntry;
import com.example.dartlife.model.Reviews;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MovieReviewsTabFragment extends Fragment {

    ListView list;
    ReviewAdapter reviewAdapter;
    String mMovieId;
    ArrayList<Reviews> mReviews;
    MovieBookEntry mMovie;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;

    public MovieReviewsTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.movie_reviews_tab_fragment, container, false);
        list = v.findViewById(R.id.reviewList);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        reviewAdapter = new ReviewAdapter(getActivity(), new ArrayList<Reviews>());

        MovieInfoActivity activity = (MovieInfoActivity) getActivity();
         mMovieId = activity.getMovieId();

        mDatabase.child("movies").child(mMovieId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMovie = dataSnapshot.getValue(MovieBookEntry.class);
                if(mMovie.getReviews() != null) {
                    reviewAdapter.setReviews(mMovie.getReviews());
                }

                reviewAdapter.notifyDataSetChanged();
                list.setAdapter(reviewAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        list.setAdapter(reviewAdapter);
        return v;
    }


}
