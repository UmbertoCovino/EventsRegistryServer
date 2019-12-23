package server.web.resources.json;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;

import commons.Event;
import commons.exceptions.DateParsingException;
import commons.exceptions.ErrorCodes;
import commons.exceptions.GenericSQLException;
import commons.exceptions.InvalidEventIdException;
import commons.exceptions.InvalidUserEmailException;
import commons.exceptions.UriEncodingException;
import commons.exceptions.VoidClassFieldException;
import server.backend.EventsAccessObject;
import server.web.frontend.EventsRegistryWebApplication;

public class EventsAfterDateJSON extends ServerResource {
	
	@Get("json")
	public String getEvents() throws ParseException, InvalidEventIdException, InvalidUserEmailException {
		Gson gson = EventsRegistryWebApplication.GSON;
		
		ArrayList<Event> events = null;
		try {
			Date date = Event.DATETIME_SDF.parse(URLDecoder.decode(getAttribute("date"), "UTF-8"));
			
			events = EventsAccessObject.getEventsAfterDate(date);
		} catch (ParseException e) {
			Status status = new Status(ErrorCodes.DATE_PARSING);
			setStatus(status);
			
			return gson.toJson(new DateParsingException(e.getMessage()), VoidClassFieldException.class);
		} catch (UnsupportedEncodingException e) {
			Status status = new Status(ErrorCodes.URI_ENCODING);
			setStatus(status);
			
			return gson.toJson(new UriEncodingException(e.getMessage()), VoidClassFieldException.class);
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
