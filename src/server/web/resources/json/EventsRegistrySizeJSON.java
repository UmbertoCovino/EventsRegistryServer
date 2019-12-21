package server.web.resources.json;

import java.text.ParseException;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;

import commons.ErrorCodes;
import commons.GenericSQLException;
import server.backend.EventsAccessObject;

public class EventsRegistrySizeJSON extends ServerResource {
	
	@Get
	public String getSize() throws ParseException {    	
		Gson gson = new Gson();
		
		try {
			return gson.toJson(new Integer(EventsAccessObject.getNumberOfEvents()), Integer.class);
		} catch (GenericSQLException e) {
			Status status = new Status(ErrorCodes.GENERIC_SQL);
			setStatus(status);
			
			return gson.toJson(e, GenericSQLException.class);
		}
	}
}
