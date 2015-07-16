package com.mobmundo.localmob.DAO;


import com.mobmundo.localmob.entity.Person;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

public class PersonDAO {
	
	public boolean savePerson(Person person){
		try{
			ParseObject personObj = new ParseObject("Person");
			personObj.put("name",person.getName());
			personObj.put("photo", person.getPhoto());
			ParseRelation<ParseUser> relation = personObj.getRelation("User");
			relation.add(person.getUser());
			personObj.saveInBackground();
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	
}
