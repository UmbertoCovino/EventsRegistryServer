package server.web.resources.json;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;

import commons.ErrorCodes;
import commons.Event;
import commons.GenericSQLException;
import commons.InvalidEventIdException;
import commons.InvalidUserEmailException;
import commons.UnauthorizedUserException;
import commons.User;
import commons.VoidClassFieldException;
import server.backend.EventsAccessObject;
import server.backend.UsersAccessObject;

public class EventUserOwnerJSON extends ServerResource {
	
	@Get
    public String getUser() throws InvalidUserEmailException {
		Gson gson = new Gson();
		
		try {
			Event event = EventsAccessObject.getEvent(Integer.valueOf(getAttribute("id")));
			
			return gson.toJson(UsersAccessObject.getUser(event.getOwnerEmail()).cloneWithoutPassword(), User.class);   	
		} catch (InvalidEventIdException e) {
			Status status = new Status(ErrorCodes.INVALID_EVENT_ID);
			setStatus(status);
			
			return gson.toJson(e, InvalidEventIdException.class);
		}
    }
    
    @Put
    public String updateUser(String payload) {
		Gson gson = new Gson();
		
		try {
			Event event = EventsAccessObject.getEvent(Integer.valueOf(getAttribute("id")));
			
			if (!getClientInfo().getUser().getIdentifier().equals(event.getOwnerEmail()))
				throw new UnauthorizedUserException("You are not authorized.");
			
			event.setOwner(gson.fromJson(payload, User.class));
			EventsAccessObject.updateEvent(event);
			
			return gson.toJson("User updated for event with id " + getAttribute("id") + ".", String.class);
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
