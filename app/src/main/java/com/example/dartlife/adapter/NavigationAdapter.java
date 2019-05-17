package com.example.dartlife.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.dartlife.fragment.BookFragment;
import com.example.dartlife.fragment.EntertainmentFragment;
import com.example.dartlife.fragment.FoodFragment;
import com.example.dartlife.fragment.MovieFragment;
import com.example.dartlife.fragment.ProfileFragment;

public class NavigationAdapter extends FragmentPagerAdapter {
    private String[] mTabNames = {"Book", "Movie", "Entertainment", "Food", "Profile"};
    public NavigationAdapter(FragmentManager FM){
        super(FM);
    }

    @Override
    //return the corresponding fragment by the position
    public Fragment getItem(int pos){
        if(pos == 0){
            return new BookFragment();
        }
        else if(pos == 1){
            return new MovieFragment();
        }
        else if(pos == 2){
            return new EntertainmentFragment();
        }
        else if(pos == 3){
            return new FoodFragment();
        }
        else if(pos == 4){
            return new ProfileFragment();
        }
        return new BookFragment();
    }

    //just override the interface
    @Override
    public int getCount(){
        return mTabNames.length;
    }

    //get the title according to the position of the page
    @Override
    public CharSequence getPageTitle(int pos){
        return mTabNames[pos];
    }
}