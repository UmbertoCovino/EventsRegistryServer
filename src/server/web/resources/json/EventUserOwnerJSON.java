package server.web.resources.json;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;

import commons.ErrorCodes;
import commons.GenericSQLException;
import commons.InvalidEventIdException;
import commons.InvalidUserEmailException;
import commons.UnauthorizedUserException;
import commons.User;
import server.backend.EventsAccessObject;
import server.backend.UsersAccessObject;

public class EventUserOwnerJSON extends ServerResource {
	
	@Get
    public String getUser() throws InvalidUserEmailException {
		Gson gson = new Gson();
		
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
//			User user = gson.fromJson(payload, User.class);
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
