package com.example.dartlife.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.dartlife.model.Reviews;
import com.example.dartlife.R;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

public class ReviewAdapter extends BaseAdapter {
    private LayoutInflater inflter;
    private ArrayList<Reviews> reviews;

    public ReviewAdapter(Context applicationContext, ArrayList<Reviews> reviews) {
        this.reviews = reviews;
        inflter = (LayoutInflater.from(applicationContext));

    }
    public void setReviews(ArrayList<Reviews> reviews) {
        this.reviews = reviews;
    }

    @Override
    public int getCount() {
        return reviews.size();
    }

    @Override
    public Object getItem(int i) {
        return reviews.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint({"SetTextI18n", "ViewHolder", "InflateParams"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.review_list_view, null);
        CircularImageView userProfile =  view.findViewById(R.id.circularImageViewReview);
        TextView userName =  view.findViewById(R.id.userName);
        TextView userReview =  view.findViewById(R.id.userReview);

        Picasso.get().load(reviews.get(i).getUserUrl()).into(userProfile);
        userName.setText(reviews.get(i).getUserName());
        userReview.setText(reviews.get(i).getReview());

        return view;
    }

}
