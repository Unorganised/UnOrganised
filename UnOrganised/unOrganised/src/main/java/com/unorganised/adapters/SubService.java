package com.unorganised.adapters;

/**
 * Created by User on 28-03-2016.
 */
public class SubService {
    private boolean selected;
    private String subServiceId;
    private String subService;

    public SubService() {
    }

    public SubService(String subServiceId, String subService, boolean selected) {
        this.subServiceId = subServiceId;
        this.subService = subService;
        this.selected = selected;
    }

    public String getSubServiceId() {
        return subServiceId;
    }

    public String getSubService() {
        return subService;
    }

    public void setSubService(String subService) {
        this.subService = subService;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void toggleChecked() {
        selected = !selected ;
    }
}
