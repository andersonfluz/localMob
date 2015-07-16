package com.mobmundo.localmob.entity;

public class LocationUserShared {
	private String userId;
	private String locationSharedId;
	private Boolean itArrived;
	private Boolean allowed;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getLocationSharedId() {
		return locationSharedId;
	}
	public void setLocationSharedId(String locationSharedId) {
		this.locationSharedId = locationSharedId;
	}
	public Boolean getItArrived() {
		return itArrived;
	}
	public void setItArrived(Boolean itArrived) {
		this.itArrived = itArrived;
	}
	public Boolean getAllowed() {
		return allowed;
	}
	public void setAllowed(Boolean allowed) {
		this.allowed = allowed;
	}
	
}
