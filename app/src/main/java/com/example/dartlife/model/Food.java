package com.example.dartlife.model;

public class Food {
    private String FoodSource;
    private String mFoodType;
    private long beginTime;
    private long endTime;
    private String Title;
    private String ImageUrl;
    private String Comment;

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

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
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
}
