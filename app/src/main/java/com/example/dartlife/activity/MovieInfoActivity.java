package com.example.dartlife.activity;


import android.content.Intent;
import android.graphics.Bitmap;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.MenuItem;

import android.view.View;
import android.widget.ImageView;

import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dartlife.R;

import com.example.dartlife.adapter.TabAdapter;
import com.example.dartlife.fragment.MovieInfoTabFragment;
import com.example.dartlife.fragment.MovieReviewsTabFragment;
import com.example.dartlife.model.MovieBookEntry;
import com.example.dartlife.model.Profile;
import com.example.dartlife.model.Reviews;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.mindorks.editdrawabletext.DrawablePosition;
import com.mindorks.editdrawabletext.EditDrawableText;
import com.mindorks.editdrawabletext.onDrawableClickListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;


public class MovieInfoActivity extends AppCompatActivity {

    private static final String MOVIE_ID = "idKey";
    private static final String MOVIE_TITLE = "titleKey";
    private static final String MOVIE_YEAR = "yearKey";
    private static final String MOVIE_SCORE = "scoreKey";
    private static final String MOVIE_INFO = "infoKey";

    private static final String MOVIE_REVIEWS = "reviewsKey";
    private static final String MOVIE_URL = "urlKey";

    String mMovieId;
    String mMovieTitle;
    double mMovieScore;
    MovieBookEntry mMovie;
    ArrayList<Reviews> mMovieReviews;
    String mMovieInfo;

    String mMovieUrl;
    String mMovieYear;

    ImageView mMovieImageView;
    TextView mMovieTitleTextView;
    RatingBar mMovieScoreRatingBar;
    TextView mMovieInfoTextView;
    TextView mMovieScoreTextView;

    ConstraintLayout mLayout;

     TabAdapter adapter;
     TabLayout tabLayout;
    ViewPager viewPager;

    CircularImageView currentUserProfile;

    EditDrawableText reviewButton;

    //firebase tools
    private FirebaseDatabase mDB;
    private FirebaseAuth mAuth;

     Profile loadProfile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);


        Toolbar toolbar = findViewById(R.id.MovieInfoToolbar);
        toolbar.setTitle(" ");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        //initialize the firebase tool
        mDB = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mMovieImageView = findViewById(R.id.MoviePoster);
        mMovieTitleTextView = findViewById(R.id.MovieInfoTitle);
        mMovieScoreRatingBar = findViewById(R.id.MovieInfoScoreRating);
        mMovieInfoTextView = findViewById(R.id.MovieInfoInfo);
        mMovieScoreTextView = findViewById(R.id.MovieScoreNumber);
        reviewButton = findViewById(R.id.input_review);


        mLayout = findViewById(R.id.MovieInfoLayout);

        currentUserProfile = findViewById(R.id.circularImageView);

        mMovieTitle = getIntent().getStringExtra(MOVIE_TITLE);
        mMovieScore = getIntent().getDoubleExtra(MOVIE_SCORE, 0.0);
        mMovieInfo = getIntent().getStringExtra(MOVIE_INFO);

        //mMovieReviews = getIntent().getSerializableExtra(MOVIE_REVIEWS);
        mMovieId = getIntent().getStringExtra(MOVIE_ID);
        mMovieUrl = getIntent().getStringExtra(MOVIE_URL);
        mMovieYear = getIntent().getStringExtra(MOVIE_YEAR);

        mMovieTitleTextView.setText(mMovieTitle + " (" + mMovieYear + ")");
        mMovieScoreRatingBar.setRating((float)mMovieScore / 2.0f);
        mMovieInfoTextView.setText(mMovieInfo);
        mMovieScoreTextView.setText(String.valueOf(mMovieScore));

        reviewButton.setDrawableClickListener(new onDrawableClickListener() {
            @Override
            public void onClick(DrawablePosition drawablePosition) {
                Toast.makeText(MovieInfoActivity.this, "click", Toast.LENGTH_LONG).show();
                //submit review with 1.current user name 2. user url 3. review to firebase
                String name = loadProfile.getName();
                String userUrl = loadProfile.getImageUrl();
                String reviewText = reviewButton.getText().toString();

                Reviews review = new Reviews(userUrl, reviewText, name);
                Log.d("firebase_review", mMovieId);
               ArrayList<Reviews> all_reviews = mMovie.getReviews();
               if(all_reviews == null) {
                   all_reviews = new ArrayList<Reviews>(Arrays.asList(review));
               } else {
                   all_reviews.add(review);
               }

               //mMovie.setReviews(all_reviews);
                mDB.getReference().child("movies").child(mMovieId).child("reviews").setValue(all_reviews);
                reviewButton.getText().clear();

            }
        });

        reviewButton.clearFocus();


        Picasso.get().load(mMovieUrl).into(mMovieImageView);

        Picasso.get().load(mMovieUrl).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                BitmapDrawable background = new BitmapDrawable(bitmap);

                //background.setAlpha(100);
                background.setColorFilter(Color.rgb(123, 123, 123), PorterDuff.Mode.MULTIPLY);

                mLayout.setBackground(background);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }


            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });


        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        adapter = new TabAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        adapter.addFragment(new MovieInfoTabFragment(), "Info");
        adapter.addFragment(new MovieReviewsTabFragment(), "Reviews");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        //set the listener
        mDB.getReference().child("Profile").child(getid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadProfile = dataSnapshot.getValue(Profile.class);
                assert loadProfile != null;
                Picasso.get().load(loadProfile.getImageUrl()).into(currentUserProfile);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //get movie entry
        mDB.getReference().child("movies").child(mMovieId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMovie = dataSnapshot.getValue(MovieBookEntry.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
                Log.d("press", "press");
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void supportFinishAfterTransition() {

        super.supportFinishAfterTransition();
        finish();
    }

    //get user's id
    private String getid() {
        return String.valueOf(Objects.requireNonNull(Objects.requireNonNull(
                mAuth.getCurrentUser()).getEmail()).hashCode());
    }

    public String getMovieId() {
        return mMovieId;
    }

}
