package com.unorganised.util;


import android.graphics.Bitmap;

public class AssignedJobs {
    private int jobId;
    private int seekerId;
    private int subscribedCount;
    private double latitude;
    private double longitude;
    private double seekerLat;
    private double seekerLng;
    private String description;
    private String biddingAmount;
    private String providerMobileNo;
    private String providerMailId;
    private String providerName;
    private String seekerName;
    private String seekerMobileNumber;
    private String rating;
    private String totalRating;
    private String serviceDes;
    private String subServiceDes;
    private String createdDate;
    private String seekerAddress;
    private Bitmap seekerIcon;
    private String seekerEmailId;
    private String startDate;

    public AssignedJobs(int jobId, int seekerId, int subscribedCount, String description, double latitude, double longitude, String biddingAmount, String providerName,
                        String providerMobileNo, String providerMailId, String seekerName, String seekerMobileNumber, String rating, String totalRating, String serviceDes,
                        String subServiceDes, String createdDate,String seekerAddress, Bitmap seekerIcon, String seekerEmailId, String startDate) {
        this.jobId = jobId;
        this.seekerId = seekerId;
        this.subscribedCount = subscribedCount;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.biddingAmount = biddingAmount;
        this.providerName = providerName;
        this.providerMobileNo = providerMobileNo;
        this.providerMailId = providerMailId;
        this.seekerName = seekerName;
        this.seekerMobileNumber = seekerMobileNumber;
        this.rating = rating;
        this.totalRating = totalRating;
        this.serviceDes = serviceDes;
        this.subServiceDes = subServiceDes;
        this.createdDate = createdDate;
        this.seekerAddress = seekerAddress;
        this.seekerIcon = seekerIcon;
        this.seekerEmailId = seekerEmailId;
        this.startDate = startDate;
    }

    public AssignedJobs(int jobId, int seekerId, String seekerAddress, String seekerName, String rating, String totalRating, double seekerLat,
                        double seekerLng, String seekerMobileNumber) {
        this.jobId = jobId;
        this.seekerAddress = seekerAddress;
        this.seekerName = seekerName;
        this.rating = rating;
        this.totalRating = totalRating;
        this.seekerLat = seekerLat;
        this.seekerLng = seekerLng;
        this.seekerMobileNumber = seekerMobileNumber;
        this.seekerId = seekerId;
    }

    public int getJobId() {
        return jobId;
    }

    public int getSeekerId() {
        return seekerId;
    }

    public String getSubServiceDes() {
        return subServiceDes;
    }

    public String getDescription() {
        return description;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getBiddingAmount() {
        return biddingAmount;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getProviderMobileNo() {
        return providerMobileNo;
    }

    public String getProviderMailId() {
        return providerMailId;
    }

    public String getSeekerName() {
        return seekerName;
    }

    public String getSeekerMobileNumber() {
        return seekerMobileNumber;
    }

    public String getRating() {
        return rating;
    }

    public String getServiceDes() {
        return serviceDes;
    }

    public String getCreatedDate() {
        return createdDate;
    }


    public double getSeekerLat() {
        return seekerLat;
    }

    public double getSeekerLng() {
        return seekerLng;
    }

    public String getTotalRating() {
        return totalRating;
    }

    public String getSeekerAddress() {
        return seekerAddress;
    }

    public int getSubscribedCount() {
        return subscribedCount;
    }

    public Bitmap getSeekerIcon() {
        return seekerIcon;
    }

    public String getSeekerEmailId() {
        return seekerEmailId;
    }

    public String getStartDate() {
        return startDate;
    }
}


