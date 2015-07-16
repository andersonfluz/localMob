package com.mobmundo.localmob.DAO;

import com.mobmundo.localmob.entity.User;
import com.parse.ParseObject;

public class UserDAO {
	Boolean retorno;
	
	public boolean saveUser(User user) {
		try {
			ParseObject userObj = new ParseObject("User");
			userObj.put("username", user.getUserName());
			userObj.put("emailVerified", user.getEmail());
			userObj.put("password", user.getPassword());
			userObj.put("phone", user.getPhone());
			userObj.saveInBackground();
			return true;
		} catch (Exception e) {
			return false;
		}
	}	
}
