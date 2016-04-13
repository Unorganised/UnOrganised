package com.unorganised.util;

import android.graphics.Bitmap;

/**
 * Created by anarra on 02/01/16.
 */
public class Provider {


    private int ratingOnJob;
    private int rating;
    private int ratingCount;
    private int jobId;
    private int serviceId;
    private int subServiceId;
    private long mobileNum;
    private double longitude;
    private double latitude;
    private double biddingAmt;
    private String name;
    private String address;
    private String profileImagePath;
    private String jobDesc;
    private String serviceDesc;
    private String subServiceDesc;
    private String date;
    private Bitmap profileIcon;


    public Provider(int ratingOnJob, int rating, int ratingCount, String imagePath, double longitude, double latitude, String name, String address, int jobId,
                    String jobDesc,
                    int serviceId,
                    int subServiceId,
                    String serviceDesc,
                    String subServiceDesc,
                    long mobileNum,
                    double biddingAmt, String date,
                    Bitmap profileIcon) {

        this.name = name;
        this.address = address;
        this.ratingOnJob = ratingOnJob;
        this.rating = rating;
        this.ratingCount = ratingCount;
        this.profileImagePath = imagePath;
        this.longitude = longitude;
        this.latitude = latitude;
        this.jobId = jobId;
        this.jobDesc = jobDesc;
        this.serviceId = serviceId;
        this.subServiceId = subServiceId;
        this.serviceDesc = serviceDesc;
        this.subServiceDesc = subServiceDesc;
        this.mobileNum = mobileNum;
        this.biddingAmt = biddingAmt;
        this.date = date;
        this.profileIcon = profileIcon;
    }

    public String getAddress() {
        return address;
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getSubServiceId() {
        return subServiceId;
    }

    public void setSubServiceId(int subServiceId) {
        this.subServiceId = subServiceId;
    }

    public String getServiceDesc() {
        return serviceDesc;
    }

    public void setServiceDesc(String serviceDesc) {
        this.serviceDesc = serviceDesc;
    }

    public String getSubServiceDesc() {
        return subServiceDesc;
    }

    public void setSubServiceDesc(String subServiceDesc) {
        this.subServiceDesc = subServiceDesc;
    }

    public long getMobileNum() {
        return mobileNum;
    }

    public void setMobileNum(long mobileNum) {
        this.mobileNum = mobileNum;
    }

    public double getBiddingAmt() {
        return biddingAmt;
    }

    public void setBiddingAmt(double biddingAmt) {
        this.biddingAmt = biddingAmt;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Bitmap getProfileIcon() {
        return profileIcon;
    }
}
