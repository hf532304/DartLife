package com.example.dartlife.fragment;

import android.content.Intent;
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
import android.widget.Toast;

import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.dartlife.activity.MovieInfoActivity;
import com.example.dartlife.model.Reviews;
import com.mancj.materialsearchbar.MaterialSearchBar;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

public class MovieFragment extends Fragment implements
        CustomAdapter. MyCallBack ,MaterialSearchBar.OnSearchActionListener {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUserId;

    MultiStateToggleButton filter_area;
    MultiStateToggleButton filter_type;
    MaterialSearchBar search_bar;

    private static final String MOVIE_ID = "idKey";
    private static final String MOVIE_TITLE = "titleKey";
    private static final String MOVIE_YEAR = "yearKey";
    private static final String MOVIE_SCORE = "scoreKey";
    private static final String MOVIE_INFO = "infoKey";
    private static final String MOVIE_DESCRIPTION = "descriptionKey";
    private static final String MOVIE_REVIEWS = "reviewsKey";
    private static final String MOVIE_URL = "urlKey";


    CustomAdapter customAdapter;
    RecyclerView list;
    ArrayList<MovieBookEntry> movies;
    public MovieFragment() {
        // Required empty public constructor
    }

    public void listenerMethod(MultiStateToggleButton category_first, MaterialSearchBar search_bar_input){
        filter_area = category_first;
        search_bar = search_bar_input;
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

            search_bar.setOnSearchActionListener(this);
            search_bar.setCardViewElevation(10);



    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        String s = enabled ? "enabled" : "disabled";
        Toast.makeText(getActivity(), "Search " + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        //startSearch(text.toString(), true, null, true);
        //Toast.makeText(getActivity(),  text.toString(), Toast.LENGTH_SHORT).show();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child("movies").orderByChild("title").equalTo(text.toString());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        // do something with the individual "issues"
                        MovieBookEntry data = issue.getValue(MovieBookEntry.class);
                        Toast.makeText(getActivity(), data.getInfo(), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getActivity(), MovieInfoActivity.class);
                        Log.d("firebase_review", data.getId());
                        intent.putExtra(MOVIE_ID, data.getId());
                        intent.putExtra(MOVIE_TITLE, data.getTitle());
                        intent.putExtra(MOVIE_INFO, data.getInfo());
                        intent.putExtra(MOVIE_SCORE, data.getScore());
                        intent.putExtra(MOVIE_DESCRIPTION, data.getDescription());
                        //intent.putExtra(MOVIE_REVIEWS, data.getReviews());
                        intent.putExtra(MOVIE_URL, data.getUrl());
                        intent.putExtra(MOVIE_YEAR, data.getYear());

                        startActivity(intent);


                    }

                }
                else {
                    Toast.makeText(getActivity(), "No result.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("buttonhaha", "oncancelled");

            }
        });


    }

    @Override
    public void onButtonClicked(int buttonCode) {
        switch (buttonCode){
            case MaterialSearchBar.BUTTON_NAVIGATION:

                break;
            case MaterialSearchBar.BUTTON_SPEECH:

        }
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

        //parseDataFromFile();




        return v;

    }


    public void parseDataFromFile() {
        // Reading text file from assets folder
        Log.d("movie_json", "execute");
        ArrayList<String> movieTitles = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(getContext().getAssets().open(
                    "movie_info.txt")));
            String temp;
            while ((temp = br.readLine()) != null)
                sb.append(temp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close(); // stop reading
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String myjsonstring = sb.toString();
        //Log.d("movie_json", myjsonstring);

        // Try to parse JSON
        try {

            //JSONObject jsonObjMain = new JSONObject(myjsonstring);


            //JSONArray jsonArray = jsonObjMain.getJSONArray();
            JSONArray jsonArray = new JSONArray(myjsonstring);
            Log.d("movie_json", "length " + jsonArray.length());


            for (int i = 0; i < jsonArray.length(); i++) {
                //Log.d("movie_json", "object" + jsonArray.getString(i));

                JSONObject jsonObj = new JSONObject(jsonArray.getString(i));


                String title = jsonObj.getString("title");
                String description = jsonObj.getString("description");
                String playTime = jsonObj.getString("play_time");

                Log.d("movie_json", title);
                if(!movieTitles.contains(title)) {

                    movieTitles.add(title);
                    getMoviesFromServer(title, description, playTime);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void saveDataToFirebase(MovieBookEntry movie) {
        mDatabase.child("movies").child(movie.getId()).setValue(movie);
    }

    private void getMoviesFromServer(final String movieTitle, final String descrip, final String playtime) {

        String url = "http://www.omdbapi.com/?apikey=1ba5c826&t=";
        movieTitle.replace(" ", "+");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url+movieTitle, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String title = response.getString("Title");
                    String id = response.getString("imdbID");
                    Double score;
                    if (response.getString("imdbRating").equals("N/A")) {
                        score = 0.0;
                    } else {
                        score = Double.valueOf(response.getString("imdbRating"));
                    }

                    String movieOrBook = response.getString("Type");
                    String type = response.getString("Genre").split(",")[0];
                     String area = response.getString("Country").split(",")[0];
                     String description = descrip;
                     String info = "Director: "+response.getString("Director") + "\n" + "Writer: "+response.getString("Writer") + "\n" + "Actors: "+response.getString("Actors");
                     String year = response.getString("Year");
                     String url = response.getString("Poster");
                     String playTime = playtime;
                     MovieBookEntry movie = new MovieBookEntry(id, title, score, movieOrBook, new ArrayList<Reviews>(), type, area, description, info, year, url, playtime);
                    saveDataToFirebase(movie);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //handle error
                error.printStackTrace();
            }
        });


        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }



}
