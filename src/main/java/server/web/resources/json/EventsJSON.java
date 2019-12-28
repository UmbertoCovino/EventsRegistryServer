package server.web.resources.json;

import java.text.ParseException;
import java.util.ArrayList;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import commons.Event;
import commons.exceptions.ErrorCodes;
import commons.exceptions.GenericSQLException;
import commons.exceptions.InvalidEventIdException;
import commons.exceptions.InvalidUserEmailException;
import commons.exceptions.JsonParsingException;
import commons.exceptions.UnauthorizedUserException;
import commons.exceptions.VoidClassFieldException;
import server.backend.EventsAccessObject;
import server.web.frontend.EventsRegistryWebApplication;

public class EventsJSON extends ServerResource {
	
	@Get("json")
	public String getEvents() throws ParseException {   	
		Gson gson = EventsRegistryWebApplication.GSON;
		
		ArrayList<Event> events = null;
		try {
			events = EventsAccessObject.getEvents();
		} catch (GenericSQLException e) {
			Status status = new Status(ErrorCodes.GENERIC_SQL);
			setStatus(status);
			
			return gson.toJson(e, GenericSQLException.class);
		}
		
		return gson.toJson(events.toArray(new Event[events.size()]), Event[].class);
	}
    
    @Post("json")
    public String addEvent(String payload) throws ParseException, InvalidUserEmailException {   	
    		Gson gson = EventsRegistryWebApplication.GSON;
		
		Event event;
		try {
			event = gson.fromJson(payload, Event.class);
		} catch (JsonSyntaxException e) {
			Status status = new Status(ErrorCodes.JSON_PARSING);
			setStatus(status);
			
			return gson.toJson(new JsonParsingException(e.getMessage()), JsonParsingException.class);
		}
		
		try {
			event.setOwnerEmail(getClientInfo().getUser().getIdentifier());
			
			int lastInsertedId = EventsAccessObject.addEvent(event);
			
			String photoPath = lastInsertedId + ".jpg";
			
			EventsAccessObject.updateEventPhotoPath(lastInsertedId, photoPath);
			
//			return gson.toJson("Event with id " + event.getId() + " added.", String.class);
			return gson.toJson(lastInsertedId, int.class);
//			return gson.toJson(event, Event.class);
		} catch (InvalidEventIdException e) {
			Status status = new Status(ErrorCodes.INVALID_EVENT_ID);
			setStatus(status);
			
			return gson.toJson(e, InvalidEventIdException.class);
		} catch (VoidClassFieldException e) {
			Status status = new Status(ErrorCodes.VOID_CLASS_FIELD);
			setStatus(status);
			
			return gson.toJson(e, VoidClassFieldException.class);
		} catch (GenericSQLException e) {
			Status status = new Status(ErrorCodes.GENERIC_SQL);
			setStatus(status);
			
			return gson.toJson(e, GenericSQLException.class);
		}
    }
    
    @Put("json")
    public String updateEvent(String payload) throws ParseException, InvalidEventIdException {
    		Gson gson = EventsRegistryWebApplication.GSON;
    		
    		Event event;
    		try {
    			event = gson.fromJson(payload, Event.class);
    		} catch (JsonSyntaxException e) {
    			Status status = new Status(ErrorCodes.JSON_PARSING);
    			setStatus(status);
    			
    			return gson.toJson(new JsonParsingException(e.getMessage()), JsonParsingException.class);
    		}
    		
		try {
			if (!getClientInfo().getUser().getIdentifier().equals(event.getOwnerEmail()))
				throw new UnauthorizedUserException("You are not authorized.");
			
			EventsAccessObject.updateEvent(event);
			
			return gson.toJson(true, boolean.class);
//			return gson.toJson("Event with id " + event.getId() + " updated.", String.class);
		} catch (UnauthorizedUserException e) {
			Status status = new Status(ErrorCodes.UNAUTHORIZED_USER);
			setStatus(status);
			
			return gson.toJson(e, UnauthorizedUserException.class);
		} catch (VoidClassFieldException e) {
			Status status = new Status(ErrorCodes.VOID_CLASS_FIELD);
			setStatus(status);
			
			return gson.toJson(e, VoidClassFieldException.class);
		} catch (GenericSQLException e) {
			Status status = new Status(ErrorCodes.GENERIC_SQL);
			setStatus(status);
			
			return gson.toJson(e, GenericSQLException.class);
		}
    }
    
//    @Delete("json")
//    public String deleteAll() {
//		//to be implemented
//		
//		return null;
//    }
}
