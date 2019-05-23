package com.example.dartlife.activity;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dartlife.R;
import com.example.dartlife.model.Entertainment;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class EntertainmentInfoActivity extends AppCompatActivity {
    private TextView mEntertainmentDetailDateTimeView;
    private TextView mEntertainmentDetailTitleView;
    private TextView mEntertainmentDetailTypeView;
    private TextView mEntertainmentDetailCommentView;
    private ImageView mEntertainmentDetailImageView;

    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entertainment_info);

        String EntertainmentStr = Objects.requireNonNull(getIntent().getExtras()).getString("entertainment");
        Entertainment theEntertainment = new Gson().fromJson(EntertainmentStr, Entertainment.class);

        //initialization of the widget
        mEntertainmentDetailCommentView = findViewById(R.id.EntertainmentDetailComment);
        mEntertainmentDetailDateTimeView = findViewById(R.id.EntertainmentDetailDateTimeView);
        mEntertainmentDetailTitleView = findViewById(R.id.EntertainmentDetailTitleView);
        mEntertainmentDetailTypeView = findViewById(R.id.EntertainmentDetailTypeView);
        mEntertainmentDetailImageView = findViewById(R.id.EntertainmentDetailImageView);

        //the setting of toolbar
        Objects.requireNonNull(getSupportActionBar()).setTitle("Free Entertainment Information");
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //the setting of views
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(theEntertainment.getTime());
        mEntertainmentDetailDateTimeView.setText("Time: " + new SimpleDateFormat("yyyy-MM-dd : HH:mm").
                format(cal.getTime()));
        Picasso.get()
                .load(theEntertainment.getImageUrl())
                .into(mEntertainmentDetailImageView);
        mEntertainmentDetailTitleView.setText("Title: " + theEntertainment.getTitle());
        mEntertainmentDetailTypeView.setText("EntertainmentType: "+ theEntertainment.getActivityType());
        mEntertainmentDetailCommentView.setText("Description: " + theEntertainment.getDescription());

        //setting the dialog
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
