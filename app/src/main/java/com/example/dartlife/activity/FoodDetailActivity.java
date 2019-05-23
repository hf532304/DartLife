package com.example.dartlife.activity;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dartlife.R;
import com.example.dartlife.model.Food;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class FoodDetailActivity extends AppCompatActivity {
    private TextView mFoodDetailDateTimeView;
    private TextView mFoodDetailTitleView;
    private TextView mFoodDetailFoodTypeView;
    private TextView mFoodDetailCommentView;
    private ImageView mFoodDetailImageView;

    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        String foodStr = Objects.requireNonNull(getIntent().getExtras()).getString("Food");
        Food theFood = new Gson().fromJson(foodStr, Food.class);

        //initialization of the widget
        mFoodDetailCommentView = findViewById(R.id.FoodDetailComment);
        mFoodDetailDateTimeView = findViewById(R.id.FoodDetailDateTimeView);
        mFoodDetailTitleView = findViewById(R.id.FoodDetailTitleView);
        mFoodDetailFoodTypeView = findViewById(R.id.FoodDetailFoodTypeView);
        mFoodDetailImageView = findViewById(R.id.FoodDetailImageView);

        //the setting of toolbar
        Objects.requireNonNull(getSupportActionBar()).setTitle("Free Food Information");
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //the setting of views
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(theFood.getTime());
        mFoodDetailDateTimeView.setText("Time: " + new SimpleDateFormat("yyyy-MM-dd : HH:mm").
                format(cal.getTime()));
        Picasso.get()
                .load(theFood.getImageUrl())
                .into(mFoodDetailImageView);
        mFoodDetailTitleView.setText("Title: " + theFood.getTitle());
        mFoodDetailFoodTypeView.setText("FoodType: "+ theFood.getmFoodType());
        mFoodDetailCommentView.setText("Comment: " + theFood.getComment());

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
