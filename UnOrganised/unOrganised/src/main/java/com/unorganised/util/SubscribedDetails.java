package com.unorganised.util;

/**
 * Created by Dell on 2/2/2016.
 */
public class SubscribedDetails {


    private long id;
    private String providerName;
    private String service;
    private String subscribed_From;
    private String subscribed_To;
    private String frequency;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getSubscribed_From() {
        return subscribed_From;
    }

    public void setSubscribed_From(String subscribed_from) {
        this.subscribed_From = subscribed_from;
    }

    public String getSubscribed_To() {
        return subscribed_To;
    }

    public void setSubscribed_To(String subscribed_To) {
        this.subscribed_To = subscribed_To;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

public SubscribedDetails(long id,String providerName,String serive,String subscribed_From,String subscribed_To,String frequency)
{
    this.id=id;
    this.providerName=providerName;
    this.service=serive;
    this.subscribed_From=subscribed_From;
    this.subscribed_To=subscribed_To;
    this.frequency=frequency;
}
}

