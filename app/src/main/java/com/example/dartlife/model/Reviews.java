package com.example.dartlife.model;

public class Reviews {
    private String userUrl;
    private String review;
    private String userName;

    public Reviews () {

    }

    public Reviews(String url, String review, String username) {
        this.userUrl = url;
        this.review = review;
        this.userName = username;
    }

    public String getUserUrl() {
        return userUrl;
    }

    public void setUserUrl(String userUrl) {
        this.userUrl = userUrl;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
