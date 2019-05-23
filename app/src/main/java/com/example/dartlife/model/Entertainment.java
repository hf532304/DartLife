package com.example.dartlife.model;

import com.google.android.gms.maps.model.LatLng;

public class Entertainment {
    private String ActivityType;
    private long Time;
    private String Title;
    private String ImageUrl;
    private String Description;
    private int Number;
    private LatLng Location;

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
}
