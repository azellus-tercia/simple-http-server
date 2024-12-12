package com.azellustercia.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class Song implements Serializable {
    private final String name;
    private final List<String> composers;
    private final List<String> authors;
    private final String singer;
    private final int length;
    private final String user;
    private double averageGrade;
    private List<Comment> comments;
    private HashMap<String, Integer> ratings;

    public Song(String name, List<String> composers, List<String> authors, String singer, int length, String user, double averageGrade, List<Comment> comments, HashMap<String, Integer> ratings) {
        this.name = name;
        this.composers = composers;
        this.authors = authors;
        this.singer = singer;
        this.length = length;
        this.user = user;
        this.averageGrade = averageGrade;
        this.comments = comments;
        this.ratings = ratings;
    }

    public String getName() {
        return name;
    }

    public List<String> getComposers() {
        return composers;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public String getSinger() {
        return singer;
    }

    public int getLength() {
        return length;
    }

    public String getUser() {
        return user;
    }

    public double getAverageGrade() {
        return averageGrade;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public HashMap<String, Integer> getRatings() {
        return ratings;
    }

    public void setAverageGrade(double averageGrade) {
        this.averageGrade = averageGrade;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void setRatings(HashMap<String, Integer> ratings) {
        this.ratings = ratings;
    }
}
