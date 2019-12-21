package server.web.resources.json;

import java.text.ParseException;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;

import commons.Event;
import commons.GenericSQLException;
import commons.ErrorCodes;
import commons.InvalidEventIdException;
import commons.UnauthorizedUserException;
import commons.VoidClassFieldException;
import server.backend.EventsAccessObject;

public class EventTitleJSON extends ServerResource {
	
    @Get
    public String getTitle() throws ParseException {
		Gson gson = new Gson();
		
		try {
			Event event = EventsAccessObject.getEvent(Integer.valueOf(getAttribute("id")));
		
			return gson.toJson(event.getTitle(), String.class);   	
		} catch (InvalidEventIdException e) {
			Status status = new Status(ErrorCodes.INVALID_EVENT_ID);
			setStatus(status);
			
			return gson.toJson(e, InvalidEventIdException.class);
		}
    }
    
    @Put
    public String updateTitle(String payload) {
		Gson gson = new Gson();
		
		try {
			Event event = EventsAccessObject.getEvent(Integer.valueOf(getAttribute("id")));
			
			if (!getClientInfo().getUser().getIdentifier().equals(event.getOwnerEmail()))
				throw new UnauthorizedUserException("You are not authorized.");
			
			event.setTitle(gson.fromJson(payload, String.class));
			EventsAccessObject.updateEvent(event);
			
			return gson.toJson("Title updated for event with id " + getAttribute("id") + ".", String.class);
		} catch (InvalidEventIdException e) {
			Status status = new Status(ErrorCodes.INVALID_EVENT_ID);
			setStatus(status);
			
			return gson.toJson(e, InvalidEventIdException.class);
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
}
