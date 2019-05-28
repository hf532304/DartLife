package com.example.dartlife.model;

import java.net.URL;
import java.util.ArrayList;

public class MovieBookEntry {
    private String id;
    private String title;
    private double score;
    private String movieOrBook;
    private ArrayList<Reviews> reviews;
    private String type;
    private String area;
    private String description;
    private String info;
    private String year;
    private String url;
    private String playTime;

    public MovieBookEntry() {

    }

    public MovieBookEntry(String id, String title, double score, String movieOrBook, ArrayList<Reviews> reviews, String type, String area, String description, String info, String year, String picture, String playtime) {
        this.id = id;
        this.title = title;
        this.score = score;
        this.movieOrBook = movieOrBook;
        this.reviews = reviews;
        this.type = type;
        this.area = area;
        this.description = description;
        this.info = info;
        this.year = year;
        this.url = picture;
        this.playTime = playtime;
    }

    public MovieBookEntry(String id, String title, double score, String movieOrBook, ArrayList<Reviews> reviews, String type, String area, String description, String info, String year) {
        this.id = id;
        this.title = title;
        this.score = score;
        this.movieOrBook = movieOrBook;
        this.reviews = reviews;
        this.type = type;
        this.area = area;
        this.description = description;
        this.info = info;
        this.year = year;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getMovieOrBook() {
        return movieOrBook;
    }

    public void setMovieOrBook(String movieOrBook) {
        this.movieOrBook = movieOrBook;
    }

    public ArrayList<Reviews> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Reviews> reviews) {
        this.reviews = reviews;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPlayTime() {
        return playTime;
    }

    public void setPlayTime(String playTime) {
        this.playTime = playTime;
    }
}
