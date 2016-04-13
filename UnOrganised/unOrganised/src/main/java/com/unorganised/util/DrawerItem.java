package com.unorganised.util;

public class DrawerItem {

	boolean isNotification;
	int imgId;
	String title;



	public DrawerItem(String title, int imgId, boolean isNotification) {
		this.imgId = imgId;
		this.title = title;
		this.isNotification = isNotification;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getImgId() {
		return imgId;
	}
	public void setImgId(int imgId) {
		this.imgId = imgId;
	}
	public boolean getIsNotification() {
		return isNotification;
	}
	public void setIsNotification(boolean isNotification) {
		this.isNotification = isNotification;
	}
}
