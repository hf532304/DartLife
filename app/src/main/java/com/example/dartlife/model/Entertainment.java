package com.example.dartlife.model;

import com.google.android.gms.maps.model.LatLng;

public class Entertainment {
    private String ActivityType;
    private long Starttime;
    private long Endtime;
    private String Title;
    private String ImageUrl;
    private String Description;
    private int Number;

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

    public long getStarttime() {
        return Starttime;
    }

    public void setStarttime(long starttime) {
        Starttime = starttime;
    }

    public String getActivityType() {
        return ActivityType;
    }

    public void setActivityType(String activityType) {
        ActivityType = activityType;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getNumber() {
        return Number;
    }

    public void setNumber(int number) {
        Number = number;
    }

    public long getEndtime() {
        return Endtime;
    }

    public void setEndtime(long endtime) {
        Endtime = endtime;
    }
}
