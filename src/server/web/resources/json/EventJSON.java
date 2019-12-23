package server.web.resources.json;

import java.io.File;
import java.text.ParseException;

import org.restlet.data.Status;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;

import commons.Event;
import commons.exceptions.ErrorCodes;
import commons.exceptions.GenericSQLException;
import commons.exceptions.InvalidEventIdException;
import commons.exceptions.UnauthorizedUserException;
import server.backend.EventsAccessObject;
import server.web.frontend.EventsRegistryWebApplication;

public class EventJSON extends ServerResource {
	
    @Get("json")
    public String getEvent() throws ParseException {
		Gson gson = EventsRegistryWebApplication.GSON;
		
		try {
			Event event = EventsAccessObject.getEvent(Integer.parseInt(getAttribute("id")));
			
			return gson.toJson(event, Event.class);
		} catch (InvalidEventIdException e) {
			Status status = new Status(ErrorCodes.INVALID_EVENT_ID);
			setStatus(status);
			
			return gson.toJson(e, InvalidEventIdException.class);
		} catch (GenericSQLException e) {
			Status status = new Status(ErrorCodes.GENERIC_SQL);
			setStatus(status);
			
			return gson.toJson(e, GenericSQLException.class);
		}
    }
    
    @Delete("json")
    public String deleteEvent() {
		Gson gson = EventsRegistryWebApplication.GSON;
    		int id = Integer.valueOf(getAttribute("id"));
		
		try {
			String ownerEmail = EventsAccessObject.getEventOwnerEmail(id);
			
			if (!getClientInfo().getUser().getIdentifier().equals(ownerEmail))
				throw new UnauthorizedUserException("You are not authorized.");
			
			String photoPath = EventsAccessObject.getEventPhotoPath(id);
			
			new File(EventsRegistryWebApplication.EVENTS_PHOTOS_DIRECTORY + "/" + photoPath).delete();
			
			EventsAccessObject.removeEvent(Integer.parseInt(getAttribute("id")));
			
//			return gson.toJson("Event with id " + getAttribute("id") + " removed.", String.class);
			return gson.toJson(true, boolean.class);
		} catch (InvalidEventIdException e) {
			Status status = new Status(ErrorCodes.INVALID_EVENT_ID);
			setStatus(status);
			
			return gson.toJson(e, InvalidEventIdException.class);
		} catch (UnauthorizedUserException e) {
			Status status = new Status(ErrorCodes.UNAUTHORIZED_USER);
			setStatus(status);
			
			return gson.toJson(e, UnauthorizedUserException.class);
		} catch (GenericSQLException e) {
			Status status = new Status(ErrorCodes.GENERIC_SQL);
			setStatus(status);
			
			return gson.toJson(e, GenericSQLException.class);
		}
    }
}
