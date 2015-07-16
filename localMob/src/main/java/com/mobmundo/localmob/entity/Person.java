package com.mobmundo.localmob.entity;


import com.parse.ParseFile;
import com.parse.ParseUser;

public class Person {
	private String name;	
	private ParseFile photo;
	private String phone;
	private ParseUser user;
	
	
	public Person(String name, ParseFile photo, String phone, ParseUser user){
		this.name = name;
		this.photo = photo;
		this.phone = phone;
		this.user = user;		
	}
	
	public Person(String name, ParseFile photo, String phone){
		this.name = name;
		this.photo = photo;
		this.phone = phone;		
	}
	public Person(String name, String phone){
		this.name = name;
		this.phone = phone;		
	}

	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ParseFile getPhoto() {
		return photo;
	}
	public void setPhoto(ParseFile photo) {
		this.photo = photo;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public ParseUser getUser() {
		return user;
	}

	public void setUser(ParseUser user) {
		this.user = user;
	}
}
