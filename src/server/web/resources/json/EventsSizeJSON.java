package server.web.resources.json;

import java.text.ParseException;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;

import commons.exceptions.ErrorCodes;
import commons.exceptions.GenericSQLException;
import server.backend.EventsAccessObject;
import server.web.frontend.EventsRegistryWebApplication;

public class EventsSizeJSON extends ServerResource {
	
	@Get("json")
	public String getSize() throws ParseException {    	
		Gson gson = EventsRegistryWebApplication.GSON;
		
		try {
			return gson.toJson(new Integer(EventsAccessObject.getNumberOfEvents()), Integer.class);
		} catch (GenericSQLException e) {
			Status status = new Status(ErrorCodes.GENERIC_SQL);
			setStatus(status);
			
			return gson.toJson(e, GenericSQLException.class);
		}
	}
}
