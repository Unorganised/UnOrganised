package com.unorganised.util;

/**
 * Created by anarra on 02/01/16.
 */
public class Seeker {
    private int ratingOnJob;
    private int rating;
    private int ratingCount;
    private String profileImagePath;
    private double longitude;
    private double latitude;

    public Seeker(int ratingOnJob, int rating, int ratingCount, String imagePath, double longitude, double latitude) {

        this.ratingOnJob = ratingOnJob;
        this.rating = rating;
        this.ratingCount = ratingCount;
        this.profileImagePath = imagePath;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public int getRatingOnJob() {
        return ratingOnJob;
    }

    public void setRatingOnJob(int ratingOnJob) {
        this.ratingOnJob = ratingOnJob;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
