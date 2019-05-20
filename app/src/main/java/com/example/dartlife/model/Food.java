package com.example.dartlife.model;

import com.google.android.gms.maps.model.LatLng;

public class Food {
    private String FoodSource;
    private String mFoodType;
    private long Time;
    private String Title;
    private String ImageUrl;
    private String Comment;
    private LatLng Location;

    public String getFoodSource() {
        return FoodSource;
    }

    public void setFoodSource(String foodSource) {
        FoodSource = foodSource;
    }

    public String getmFoodType() {
        return mFoodType;
    }

    public void setmFoodType(String mFoodType) {
        this.mFoodType = mFoodType;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public long getTime() {
        return Time;
    }

    public void setTime(long time) {
        Time = time;
    }

    public MyLatLng getLocation() {
        MyLatLng myLoc = new MyLatLng();
        myLoc.setLatitude(Location.latitude);
        myLoc.setLongitude(Location.longitude);
        return myLoc;
    }

    public void setLocation(MyLatLng location) {
        Location = new LatLng(location.getLatitude(), location.getLongitude());
    }
}
