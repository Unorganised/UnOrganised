package com.unorganised.util;

import android.graphics.Bitmap;

public class Job {

    private int status;
    private int totalRating;
    private float rating;
    private double longitude;
    private double latitude;
    private double biddingPrice;
    private long id;
    private long serviceId;
    private long subServiceId;
    private long providerUserId;
    private long providerNum;
    private String serviceDesc;
    private String subServiceDesc;
    private String providerName;
    private String description;
    private String address;
    private String createdDate;
    private String scheduledDate;
    private Bitmap profileImage;


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getServiceId() {
        return serviceId;
    }

    public void setServiceId(long serviceId) {
        this.serviceId = serviceId;
    }

    public long getSubServiceId() {
        return subServiceId;
    }

    public void setSubServiceId(long subServiceId) {
        this.subServiceId = subServiceId;
    }

    public long getProviderUserId() {
        return providerUserId;
    }

    public void setProviderUserId(long providerUserId) {
        this.providerUserId = providerUserId;
    }

    public long getProviderNum() {
        return providerNum;
    }

    public void setProviderNum(long providerNum) {
        this.providerNum = providerNum;
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

    public Bitmap getProfileImage() {
        return profileImage;
    }

    public Job(String providerName, String description, String address,
               double latitude, double longitude, double biddingPrice, String createdDate,
               String scheduledDate,
               long id,
               long serviceId,
               long subServiceId,
               long provideruserId,
               long providerNum,
               String serviceDesc,
               String subServiceDesc, int status, Bitmap profileImage) {
        this.providerName = providerName;
        this.description = description;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.biddingPrice = biddingPrice;
        this.createdDate = createdDate;
        this.scheduledDate = scheduledDate;
        this.id = id;
        this.serviceId = serviceId;
        this.subServiceId = subServiceId;
        this.providerUserId = provideruserId;
        this.providerNum = providerNum;
        this.serviceDesc = serviceDesc;
        this.subServiceDesc = subServiceDesc;
        this.status = status;
        this.profileImage = profileImage;
    }

    public Job(int totalRating, float rating, String providerName, String description, String address,
               double latitude, double longitude, double biddingPrice, String createdDate,
               String scheduledDate,
               long id,
               long serviceId,
               long subServiceId,
               long provideruserId,
               long providerNum,
               String serviceDesc,
               String subServiceDesc, int status, Bitmap profileImage) {
        this.totalRating = totalRating;
        this.rating = rating;
        this.providerName = providerName;
        this.description = description;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.biddingPrice = biddingPrice;
        this.createdDate = createdDate;
        this.scheduledDate = scheduledDate;
        this.id = id;
        this.serviceId = serviceId;
        this.subServiceId = subServiceId;
        this.providerUserId = provideruserId;
        this.providerNum = providerNum;
        this.serviceDesc = serviceDesc;
        this.subServiceDesc = subServiceDesc;
        this.status = status;
        this.profileImage = profileImage;
    }

    public Job(String providerName, String description, String address,
               double latitude, double longitude, double biddingPrice, String dateTime) {
        this.providerName = providerName;
        this.description = description;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.biddingPrice = biddingPrice;
        this.createdDate = dateTime;
    }

    public int getTotalRating() {
        return totalRating;
    }

    public float getRating() {
        return rating;
    }

    public String getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(String scheduledDate) {
        this.scheduledDate = scheduledDate;
    }


    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public double getBiddingPrice() {
        return biddingPrice;
    }

    public void setBiddingPrice(double biddingPrice) {
        this.biddingPrice = biddingPrice;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
