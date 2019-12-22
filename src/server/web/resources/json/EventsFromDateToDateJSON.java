package server.web.resources.json;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;

import commons.Event;
import commons.GenericSQLException;
import commons.InvalidEventIdException;
import commons.InvalidUserEmailException;
import server.backend.EventsAccessObject;
import server.web.frontend.EventsRegistryWebApplication;

public class EventsFromDateToDateJSON extends ServerResource {
	
	@Get
	public String getEvents() throws ParseException, InvalidEventIdException, InvalidUserEmailException {   	
		Gson gson = EventsRegistryWebApplication.GSON;

		Date fromDate = Event.DATETIME_SDF.parse(getAttribute("from"));
		Date toDate = Event.DATETIME_SDF.parse(getAttribute("to"));
		
		ArrayList<Event> events = null;
		try {
			events = EventsAccessObject.getEventsBetweenTwoDates(fromDate, toDate);
		} catch (GenericSQLException e) {
			e.printStackTrace();
		}
		
		return gson.toJson(events.toArray(new Event[events.size()]), Event[].class);
	}
}
