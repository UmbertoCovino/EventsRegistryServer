package server.web.resources.json;

import java.text.ParseException;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;

import commons.ErrorCodes;
import commons.GenericSQLException;
import server.backend.UsersAccessObject;

public class UsersRegistrySizeJSON extends ServerResource {

	@Get
	public String getSize() throws ParseException {    	
		Gson gson = new Gson();
		
		try {
			return gson.toJson(new Integer(UsersAccessObject.getNumberOfUsers()), Integer.class);
		} catch (GenericSQLException e) {
			Status status = new Status(ErrorCodes.GENERIC_SQL);
			setStatus(status);
			
			return gson.toJson(e, GenericSQLException.class);
		}
	}
}
