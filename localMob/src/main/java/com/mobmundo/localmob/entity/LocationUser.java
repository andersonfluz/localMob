package com.mobmundo.localmob.entity;

import java.sql.Date;

import com.parse.ParseGeoPoint;

public class LocationUser {
	private String userId;
	private ParseGeoPoint geoPoint;
	private Date dateTimeLocation;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public ParseGeoPoint getGeoPoint() {
		return geoPoint;
	}
	public void setGeoPoint(ParseGeoPoint geoPoint) {
		this.geoPoint = geoPoint;
	}
	public Date getDateTimeLocation() {
		return dateTimeLocation;
	}
	public void setDateTimeLocation(Date dateTimeLocation) {
		this.dateTimeLocation = dateTimeLocation;
	}

	
	
}
