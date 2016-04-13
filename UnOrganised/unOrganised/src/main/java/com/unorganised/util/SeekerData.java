package com.unorganised.util;

import android.graphics.Bitmap;

/**
 * Created by User on 24-12-2015.
 */
public class SeekerData {
    private int subscriptionStatus;
    private int jobId;
    private int seekerId;
    private int status;
    private int totalRating;
    private double latitude;
    private double longitude;
    private double seekerlat;
    private double seekerlng;
    private long mobileNumber;
    private float rating;
    private String fullName;
    private String jobTitle;
    private String date;
    private String biddingPrice;
    private String seekerAddress;
    private String paymentType;
    private Bitmap imageData;

    public SeekerData(int jobId, int seekerId, int status, int totalRating, double seekerlat, double seekerlng, String fullName, long mobileNumber,
                      float rating, Bitmap imageData, String seekerAddress) {
        this.jobId = jobId;
        this.seekerId = seekerId;
        this.status = status;
        this.fullName = fullName;
        this.mobileNumber = mobileNumber;
        this.rating = rating;
        this.imageData = imageData;
        this.seekerlat = seekerlat;
        this.seekerlng = seekerlng;
        this.seekerAddress = seekerAddress;
        this.totalRating = totalRating;
    }

    public SeekerData(int jobId, int seekerId, double latitude, double longitude, float rating, Bitmap imageData, String fullName,
                      String jobTitle, String date, String biddingPrice, String paymentType, int subscriptionStatus) {
        this.jobId = jobId;
        this.seekerId = seekerId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
        this.imageData = imageData;
        this.fullName = fullName;
        this.jobTitle = jobTitle;
        this.date = date;
        this.biddingPrice = biddingPrice;
        this.paymentType = paymentType;
        this.subscriptionStatus = subscriptionStatus;
    }

    public int getJobId() {
        return jobId;
    }

    public int getSeekerId() {
        return seekerId;
    }

    public int getStatus() {
        return status;
    }

    public String getFullName() {
        return fullName;
    }

    public long getMobileNumber() {
        return mobileNumber;
    }

    public float getRating() {
        return rating;
    }

    public Bitmap getImageData() {
        return imageData;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getDate() {
        return date;
    }

    public String getBiddingPrice() {
        return biddingPrice;
    }

    public double getSeekerlat() {
        return seekerlat;
    }

    public double getSeekerlng() {
        return seekerlng;
    }

    public int getTotalRating() {
        return totalRating;
    }

    public String getSeekerAddress() {
        return seekerAddress;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public int getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(int subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }
}
