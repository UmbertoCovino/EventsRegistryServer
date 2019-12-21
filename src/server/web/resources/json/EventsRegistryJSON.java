package server.web.resources.json;

import java.text.ParseException;
import java.util.ArrayList;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;

import commons.Event;
import commons.GenericSQLException;
import commons.ErrorCodes;
import commons.InvalidEventIdException;
import commons.InvalidUserEmailException;
import commons.UnauthorizedUserException;
import commons.VoidClassFieldException;
import server.backend.EventsAccessObject;

public class EventsRegistryJSON extends ServerResource {
	
	@Get
	public String getEvents() throws ParseException {   	
		Gson gson = new Gson();
		
		ArrayList<Event> events = null;
		try {
			events = EventsAccessObject.getEventsOrderedByStartDateAsc();
		} catch (GenericSQLException e) {
			e.printStackTrace();
		}
		
		return gson.toJson(events.toArray(new Event[events.size()]), Event[].class);
	}
    
    @Post
    public String addEvent(String payload) throws ParseException, InvalidUserEmailException {   	
		Gson gson = new Gson();
		
		Event event = gson.fromJson(payload, Event.class);
		try {
			event.setOwnerEmail(getClientInfo().getUser().getIdentifier());
			
			int lastInsertedId = EventsAccessObject.addEvent(event);
			
			String photoPath = lastInsertedId + ".jpg";
			
			EventsAccessObject.updateEventPhotoPath(lastInsertedId, photoPath);
			
//			return gson.toJson("Event with id " + event.getId() + " added.", String.class);
			return gson.toJson(lastInsertedId, String.class);
//			return gson.toJson(event, Event.class);
		} catch (InvalidEventIdException e) {
			Status status = new Status(ErrorCodes.INVALID_EVENT_ID);
			setStatus(status);
			
			return gson.toJson(e, InvalidEventIdException.class);
		}  catch (VoidClassFieldException e) {
			Status status = new Status(ErrorCodes.VOID_CLASS_FIELD);
			setStatus(status);
			
			return gson.toJson(e, VoidClassFieldException.class);
		} catch (GenericSQLException e) {
			Status status = new Status(ErrorCodes.GENERIC_SQL);
			setStatus(status);
			
			return gson.toJson(e, GenericSQLException.class);
		}
    }
    
    @Put
    public String updateEvent(String payload) throws ParseException, InvalidEventIdException {
		Gson gson = new Gson();
		
		Event event = gson.fromJson(payload, Event.class);
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
    
//    @Delete
//    public String deleteAll() {
//		//to be implemented
//		
//		return null;
//    }
}
