package com.mobmundo.localmob.DAO;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class LastLocationDAO {

	public boolean saveLocation(final ParseGeoPoint point, final String idUser) {
		try {
			ParseQuery<ParseObject> query = ParseQuery.getQuery("LastLocation");
			query.whereEqualTo("idUser", idUser);
			query.getFirstInBackground(new GetCallback<ParseObject>() {
				public void done(ParseObject object, ParseException e) {
					if (object == null) {
						ParseObject personObj = new ParseObject("LastLocation");
						personObj.put("idUser", idUser);
						personObj.put("lastLocation", point);
						personObj.saveEventually();
					} else {
						object.put("lastLocation", point);
						object.saveEventually();
					}
				}
			});
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
