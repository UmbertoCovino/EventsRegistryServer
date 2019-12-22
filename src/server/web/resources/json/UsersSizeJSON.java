package server.web.resources.json;

import java.text.ParseException;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;

import exceptions.ErrorCodes;
import exceptions.GenericSQLException;
import server.backend.UsersAccessObject;
import server.web.frontend.EventsRegistryWebApplication;

public class UsersSizeJSON extends ServerResource {

	@Get
	public String getSize() throws ParseException {    	
		Gson gson = EventsRegistryWebApplication.GSON;
		
		try {
			return gson.toJson(new Integer(UsersAccessObject.getNumberOfUsers()), Integer.class);
		} catch (GenericSQLException e) {
			Status status = new Status(ErrorCodes.GENERIC_SQL);
			setStatus(status);
			
			return gson.toJson(e, GenericSQLException.class);
		}
	}
}
