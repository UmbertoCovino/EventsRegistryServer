package server.web.resources.json;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import commons.User;
import exceptions.ErrorCodes;
import exceptions.GenericSQLException;
import exceptions.InvalidEventIdException;
import exceptions.InvalidUserEmailException;
import exceptions.JsonParsingException;
import exceptions.UnauthorizedUserException;
import server.backend.EventsAccessObject;
import server.backend.UsersAccessObject;
import server.web.frontend.EventsRegistryWebApplication;

public class EventUserOwnerJSON extends ServerResource {
	
	@Get
    public String getUser() throws InvalidUserEmailException {
		Gson gson = EventsRegistryWebApplication.GSON;
		
		try {
			User owner = EventsAccessObject.getEventOwner(Integer.valueOf(getAttribute("id")));
			
			return gson.toJson(owner, User.class);   	
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
    
	// questo metodo non so se ha senso: aggiornare l'utente associato ad un evento attraverso le uri dell'evento stesso?
//    @Put
//    public String updateUser(String payload) {
//		Gson gson = new Gson();
//		int id = Integer.valueOf(getAttribute("id"));
//		
//		try {
//			String ownerEmail = EventsAccessObject.getEventOwnerEmail(id);
//			
//			if (!getClientInfo().getUser().getIdentifier().equals(ownerEmail))
//				throw new UnauthorizedUserException("You are not authorized.");
//			
//			User user;
//			try {
//				user = gson.fromJson(payload, User.class);
//			} catch (JsonSyntaxException e) {
//	    			Status status = new Status(ErrorCodes.JSON_PARSING);
//	    			setStatus(status);
//	    			
//	    			return gson.toJson(new JsonParsingException(e.getMessage()), JsonParsingException.class);
//	    		}
//			
//			UsersAccessObject.updateUser(user);
//			
//			return gson.toJson("User updated for event with id " + id + ".", String.class);
//		} catch (InvalidEventIdException e) {
//			Status status = new Status(ErrorCodes.INVALID_EVENT_ID);
//			setStatus(status);
//			
//			return gson.toJson(e, InvalidEventIdException.class);
//		} catch (UnauthorizedUserException e) {
//			Status status = new Status(ErrorCodes.UNAUTHORIZED_USER);
//			setStatus(status);
//			
//			return gson.toJson(e, UnauthorizedUserException.class);
//		} catch (GenericSQLException e) {
//			Status status = new Status(ErrorCodes.GENERIC_SQL);
//			setStatus(status);
//			
//			return gson.toJson(e, GenericSQLException.class);
//		} catch (InvalidUserEmailException e) {
//			Status status = new Status(ErrorCodes.INVALID_USER_EMAIL);
//			setStatus(status);
//			
//			return gson.toJson(e, InvalidUserEmailException.class);
//		}
//	}
}
