package com.example.dartlife.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.dartlife.R;
import com.example.dartlife.adapter.CustomAdapter;
import com.example.dartlife.model.MovieBookEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;
import org.honorato.multistatetogglebutton.ToggleButton;

import java.util.ArrayList;

public class MovieFragment extends Fragment implements
        CustomAdapter. MyCallBack {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUserId;

    MultiStateToggleButton filter_area;
    MultiStateToggleButton filter_type;

    CustomAdapter customAdapter;
    RecyclerView list;
    ArrayList<MovieBookEntry> movies;
    public MovieFragment() {
        // Required empty public constructor
    }

    public void listenerMethod(MultiStateToggleButton category_first){
        filter_area = category_first;
        Log.d("buttonhaha", "listen");
        filter_area.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
                @Override
                public void onValueChanged(int position) {
                    Log.d("buttonhaha", "Position: " + filter_area.getTexts()[position].toString());
                    //update the movie list
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    movies = new ArrayList<>();

                    if(filter_area.getTexts()[position].toString().equalsIgnoreCase("ALL AREA")) {
                        mDatabase.child("movies").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot datasnapshotfirst : dataSnapshot.getChildren()) {

                                    if(datasnapshotfirst == null) return;
                                    MovieBookEntry movie = datasnapshotfirst.getValue(MovieBookEntry.class);
                                    movies.add(movie);
                                }
                                customAdapter.setMovies(movies);
                                customAdapter.notifyDataSetChanged();
                                //list.setAdapter(customAdapter);


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    Query query = reference.child("movies").orderByChild("area").equalTo(filter_area.getTexts()[position].toString());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {


                                for (DataSnapshot issue : dataSnapshot.getChildren()) {
                                    // do something with the individual "issues"
                                    MovieBookEntry data = issue.getValue(MovieBookEntry.class);
                                    Log.d("buttonhaha", data.getTitle());
                                    movies.add(data);

                                }
                                customAdapter.setMovies(movies);
                                Log.d("buttonhaha", "movies size" + movies.size());

                                customAdapter.notifyDataSetChanged();
                                //list.setAdapter(customAdapter);


                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d("buttonhaha", "oncancelled");

                        }
                    });

                    customAdapter.setMovies(movies);
                    Log.d("buttonhaha", "movies size" + movies.size());

                    customAdapter.notifyDataSetChanged();
                    //list.setAdapter(customAdapter);

                }
            });

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_movie, container, false);
        list = v.findViewById(R.id.movie_view);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if(mFirebaseUser != null) mUserId = mFirebaseUser.getUid();

        movies = new ArrayList<>();



        list.setLayoutManager(new LinearLayoutManager(getActivity()));


/*
        mDatabase.child("movies").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot datasnapshotfirst : dataSnapshot.getChildren()) {

                    if(datasnapshotfirst == null) return;
                    MovieBookEntry movie = datasnapshotfirst.getValue(MovieBookEntry.class);
                    movies.add(movie);
                }
                customAdapter.setMovies(movies);
                customAdapter.notifyDataSetChanged();
                list.setAdapter(customAdapter);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/



        customAdapter = new CustomAdapter(this.getActivity(), this);

        customAdapter.setMovies(movies);
        list.setAdapter(customAdapter);




        return v;

    }



}
