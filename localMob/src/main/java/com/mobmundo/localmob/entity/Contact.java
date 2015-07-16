package com.mobmundo.localmob.entity;

import com.parse.ParseUser;

public class Contact {	
	private String userContact;
	private String user;
	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getUserContact() {
		return userContact;
	}
	public void setUserContact(String userContact) {
		this.userContact = userContact;
	}
}
