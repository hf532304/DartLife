package com.example.dartlife.activity;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.dartlife.R;
import com.example.dartlife.adapter.NavigationAdapter;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {


    protected NavigationAdapter mAdapter;
    protected ViewPager mViewPager;
    protected TabLayout.Tab mBookTab;
    protected TabLayout.Tab mMovieTab;
    protected TabLayout.Tab mEntertainmentTab;
    protected TabLayout.Tab mFoodTab;
    protected TabLayout.Tab mProfileTab;
    protected TabLayout mTabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView(){
        //binding the ViewPager and MyFragmentPageAdapter

        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
        mViewPager = findViewById(R.id.viewPager);
        mAdapter = new NavigationAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);

        //then, binding the tablayout and ViewPager
        mTabLayout = findViewById(R.id.tablayout);
        mTabLayout.setupWithViewPager(mViewPager);

        //assign the tab to the position
        mBookTab = mTabLayout.getTabAt(0);
        mMovieTab = mTabLayout.getTabAt(1);
        mEntertainmentTab = mTabLayout.getTabAt(2);
        mFoodTab = mTabLayout.getTabAt(3);
        mProfileTab = mTabLayout.getTabAt(4);

        mTabLayout.setScrollPosition(0,0f,true);
        mViewPager.setCurrentItem(0);

        mBookTab.setIcon(R.drawable.book);
        mMovieTab.setIcon(R.drawable.movie);
        mEntertainmentTab.setIcon(R.drawable.entertainment);
        mFoodTab.setIcon(R.drawable.food);
        mProfileTab.setIcon(R.drawable.profile);


        mTabLayout.addOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {

                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);
                        int tabIconColor = ContextCompat.getColor(getBaseContext(), R.color.colorBlue);
                        Objects.requireNonNull(tab.getIcon()).setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        super.onTabUnselected(tab);
                        int tabIconColor = ContextCompat.getColor(getBaseContext(), R.color.colorBlack);
                        Objects.requireNonNull(tab.getIcon()).setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {
                        super.onTabReselected(tab);
                    }
                });
    }

    public void onBackPressed() {
        moveTaskToBack(true);
        super.onBackPressed();
    }
}
