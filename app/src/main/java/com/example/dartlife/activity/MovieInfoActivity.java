package com.example.dartlife.activity;

import android.annotation.SuppressLint;
import android.media.Rating;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.dartlife.R;
import com.example.dartlife.model.Food;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class MovieInfoActivity extends AppCompatActivity {

    private static final String MOVIE_ID = "idKey";
    private static final String MOVIE_TITLE = "titleKey";
    private static final String MOVIE_YEAR = "yearKey";
    private static final String MOVIE_SCORE = "scoreKey";
    private static final String MOVIE_INFO = "infoKey";
    private static final String MOVIE_DESCRIPTION = "descriptionKey";
    private static final String MOVIE_REVIEWS = "reviewsKey";
    private static final String MOVIE_URL = "urlKey";

    String mMovieTitle;
    double mMovieScore;
    ArrayList<String> mMovieReviews;
    String mMovieInfo;
    String mMovieDescription;
    String mMovieUrl;
    String mMovieYear;

    ImageView mMovieImageView;
    TextView mMovieTitleTextView;
    RatingBar mMovieScoreRatingBar;
    TextView mMovieInfoTextView;
    TextView mMovieScoreTextView;
    TextView mMovieDescriptionTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);

        Toolbar toolbar = findViewById(R.id.MovieInfoToolbar);
        toolbar.setTitle("Movie");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mMovieImageView = findViewById(R.id.MoviePoster);
        mMovieTitleTextView = findViewById(R.id.MovieInfoTitle);
        mMovieScoreRatingBar = findViewById(R.id.MovieInfoScoreRating);
        mMovieInfoTextView = findViewById(R.id.MovieInfoInfo);
        mMovieScoreTextView = findViewById(R.id.MovieScoreNumber);
        mMovieDescriptionTextView = findViewById(R.id.MovieDescription);

        mMovieTitle = getIntent().getStringExtra(MOVIE_TITLE);
        mMovieScore = getIntent().getDoubleExtra(MOVIE_SCORE, 0.0);
        mMovieInfo = getIntent().getStringExtra(MOVIE_INFO);
        mMovieDescription = getIntent().getStringExtra(MOVIE_DESCRIPTION);
        mMovieReviews = getIntent().getStringArrayListExtra(MOVIE_REVIEWS);
        mMovieUrl = getIntent().getStringExtra(MOVIE_URL);
        mMovieYear = getIntent().getStringExtra(MOVIE_YEAR);

        mMovieTitleTextView.setText(mMovieTitle + " (" + mMovieYear + ")");
        mMovieScoreRatingBar.setRating((float)mMovieScore);
        mMovieInfoTextView.setText(mMovieInfo);
        mMovieScoreTextView.setText(String.valueOf(mMovieScore));
        mMovieDescriptionTextView.setText(mMovieDescription);

        Picasso.get().load(mMovieUrl).into(mMovieImageView);





    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
