package com.unorganised.util;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class PendingJobs implements Parcelable{

    private int jobId;
    private int responseCount;
    private int responseStatus;
    private int serviceId;
    private int subServiceId;
    private double latitude;
    private double longitude;
    private String jobTitle;
    private String jobDescription;
    private String price;
    private String date;
    private String responses;
    private String serviceDes;
    private String subServiceDes;

    public PendingJobs(int jobId,int responseCount, int responseStatus, String jobTitle, double latitude, double longitude,String jobDescription,
            String price, String date, String responses, int serviceId, int subServiceId, String serviceDes, String subServiceDes) {
        this.jobId = jobId;
        this.responseCount = responseCount;
        this.responseStatus = responseStatus;
        this.jobTitle = jobTitle;
        this.latitude = latitude;
        this.longitude = longitude;
        this.jobDescription = jobDescription;
        this.price = price;
        this.date = date;
        this.responses = responses;
        this.serviceDes = serviceDes;
        this.subServiceDes = subServiceDes;
        this.subServiceId = subServiceId;
        this.serviceId = serviceId;
    }

    public int getJobId() {
        return jobId;
    }

    public int getResponseCount() {
        return responseCount;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public String getPrice() {
        return price;
    }

    public String getDate() {
        return date;
    }

    public String getResponses() {
        return responses;
    }

    public String getServiceDes() {
        return serviceDes;
    }

    public String getSubServiceDes() {
        return subServiceDes;
    }

    public PendingJobs(String serviceDes, String subServiceDes, double latitude, double longitude, String jobDescription, String price, int serviceId, int subServiceId){
        this.latitude = latitude;
        this.longitude = longitude;
        this.jobDescription = jobDescription;
        this.price = price;
        this.serviceDes = serviceDes;
        this.subServiceDes = subServiceDes;
        this.serviceId = serviceId;
        this.subServiceId = subServiceId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public int getSubServiceId() {
        return subServiceId;
    }

    //parcel part
    public PendingJobs(Parcel in){
        String[] data= new String[8];
        in.readStringArray(data);
        serviceDes = data[0];
        subServiceDes = data[1];
        latitude = Double.parseDouble(data[2]);
        longitude = Double.parseDouble(data[3]);
        jobDescription = data[4];
        price = data[5];
        serviceId = Integer.parseInt(data[6]);
        subServiceId = Integer.parseInt(data[7]);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {serviceDes, subServiceDes,"" + latitude,"" + longitude, jobDescription, price, "" + serviceId, "" + subServiceId});
    }

    public static final Parcelable.Creator<PendingJobs> CREATOR = new Parcelable.Creator<PendingJobs>() {

        @Override
        public PendingJobs createFromParcel(Parcel source) {
            return new PendingJobs(source);  //using parcelable constructor
        }

        @Override
        public PendingJobs[] newArray(int size) {
            return new PendingJobs[size];
        }
    };
}
