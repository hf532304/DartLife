package com.example.dartlife.activity;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.dartlife.R;

import com.example.dartlife.adapter.TabAdapter;
import com.example.dartlife.fragment.MovieInfoTabFragment;
import com.example.dartlife.fragment.MovieReviewsTabFragment;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class MovieInfoActivity extends AppCompatActivity {

    private static final String MOVIE_ID = "idKey";
    private static final String MOVIE_TITLE = "titleKey";
    private static final String MOVIE_YEAR = "yearKey";
    private static final String MOVIE_SCORE = "scoreKey";
    private static final String MOVIE_INFO = "infoKey";

    private static final String MOVIE_REVIEWS = "reviewsKey";
    private static final String MOVIE_URL = "urlKey";

    String mMovieTitle;
    double mMovieScore;
    ArrayList<String> mMovieReviews;
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


        mMovieImageView = findViewById(R.id.MoviePoster);
        mMovieTitleTextView = findViewById(R.id.MovieInfoTitle);
        mMovieScoreRatingBar = findViewById(R.id.MovieInfoScoreRating);
        mMovieInfoTextView = findViewById(R.id.MovieInfoInfo);
        mMovieScoreTextView = findViewById(R.id.MovieScoreNumber);

        mLayout = findViewById(R.id.MovieInfoLayout);

        mMovieTitle = getIntent().getStringExtra(MOVIE_TITLE);
        mMovieScore = getIntent().getDoubleExtra(MOVIE_SCORE, 0.0);
        mMovieInfo = getIntent().getStringExtra(MOVIE_INFO);

        mMovieReviews = getIntent().getStringArrayListExtra(MOVIE_REVIEWS);
        mMovieUrl = getIntent().getStringExtra(MOVIE_URL);
        mMovieYear = getIntent().getStringExtra(MOVIE_YEAR);

        mMovieTitleTextView.setText(mMovieTitle + " (" + mMovieYear + ")");
        mMovieScoreRatingBar.setRating((float)mMovieScore / 2.0f);
        mMovieInfoTextView.setText(mMovieInfo);
        mMovieScoreTextView.setText(String.valueOf(mMovieScore));


        Picasso.get().load(mMovieUrl).into(mMovieImageView);

        Picasso.get().load(mMovieUrl).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                BitmapDrawable background = new BitmapDrawable(bitmap);
                //background.setAlpha(100);
                //background.setColorFilter(Color.rgb(123, 123, 123), PorterDuff.Mode.SCREEN);

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

}
