package com.example.dartlife.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.dartlife.R;
import com.stephentuso.welcome.BasicPage;
import com.stephentuso.welcome.TitlePage;
import com.stephentuso.welcome.WelcomeConfiguration;
import com.stephentuso.welcome.WelcomeHelper;

public class WelcomeActivity extends com.stephentuso.welcome.WelcomeActivity {

    @Override
    protected WelcomeConfiguration configuration() {
        return new WelcomeConfiguration.Builder(this)
                .defaultBackgroundColor(R.color.blue_background)
                .page(new BasicPage(R.drawable.ice_dragon,
                        "Welcome", "This is an app designed for Dartmouth students," +
                        "providing students some options to spend leisure time.")
                        .background(R.color.orange_background)
                )
                .page(new BasicPage(R.drawable.movie_welcome,
                        "Movie",
                        "Find the latest movies showed in Nugget and write your review!")
                        .background(R.color.red_background)
                )
                .page(new BasicPage(R.drawable.calendar_welcome,
                        "Entertainment",
                        "Match some friends to play sports together!")
                        .background(R.color.purple_background)
                )
                .page(new BasicPage(R.drawable.food_welcome,
                        "Food",
                        "Eat the delicious food near Dartmouth!")
                        .background(R.color.blue_background)
                )
                .swipeToDismiss(true)
                .build();
    }
}
