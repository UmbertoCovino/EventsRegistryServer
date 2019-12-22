package server.web.resources.json;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;

import commons.Event;
import exceptions.ErrorCodes;
import exceptions.GenericSQLException;
import exceptions.InvalidEventIdException;
import exceptions.InvalidUserEmailException;
import exceptions.VoidClassFieldException;
import server.backend.EventsAccessObject;
import server.web.frontend.EventsRegistryWebApplication;

public class EventsBeforeDateJSON extends ServerResource {
	
	@Get
	public String getEvents() throws ParseException, InvalidEventIdException, InvalidUserEmailException {   	
		Gson gson = EventsRegistryWebApplication.GSON;
		
		Date date = Event.DATETIME_SDF.parse(getAttribute("date"));
		
		ArrayList<Event> events = null;
		try {
			events = EventsAccessObject.getEventsBeforeDate(date);
		} catch (VoidClassFieldException e) {
			Status status = new Status(ErrorCodes.VOID_CLASS_FIELD);
			setStatus(status);
			
			return gson.toJson(e, VoidClassFieldException.class);
		} catch (GenericSQLException e) {
			Status status = new Status(ErrorCodes.GENERIC_SQL);
			setStatus(status);
			
			return gson.toJson(e, GenericSQLException.class);
		}
		
		return gson.toJson(events.toArray(new Event[events.size()]), Event[].class);
	}
}
