package server.web.resources.json;

import java.util.ArrayList;

import org.restlet.data.Status;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;

import commons.exceptions.ErrorCodes;
import commons.exceptions.InvalidEventIdException;
import commons.exceptions.InvalidParticipationException;
import commons.exceptions.InvalidUserEmailException;
import commons.exceptions.UnauthorizedUserException;
import commons.exceptions.VoidClassFieldException;
import commons.User;
import commons.exceptions.GenericSQLException;
import server.backend.EventsAccessObject;
import server.web.frontend.EventsRegistryWebApplication;

public class EventSubscribersJSON extends ServerResource {
	
	@Get("json")
	public String getSubscribers() {
		Gson gson = EventsRegistryWebApplication.GSON;

		ArrayList<User> subscribers = null;
		try {
			subscribers = EventsAccessObject.getEventSubscribers(Integer.valueOf(getAttribute("id")));
		} catch (InvalidEventIdException e) {
			Status status = new Status(ErrorCodes.INVALID_EVENT_ID);
			setStatus(status);
			
			return gson.toJson(e, InvalidEventIdException.class);
		} catch (GenericSQLException e) {
			Status status = new Status(ErrorCodes.GENERIC_SQL);
			setStatus(status);
			
			return gson.toJson(e, GenericSQLException.class);
		} catch (VoidClassFieldException e) {
			Status status = new Status(ErrorCodes.VOID_CLASS_FIELD);
			setStatus(status);
			
			return gson.toJson(e, VoidClassFieldException.class);
		} 
		
		return gson.toJson(subscribers.toArray(new User[subscribers.size()]), User[].class);
	}
	
	@Post("json")
	public String addSubscriber(String payload) {
		Gson gson = EventsRegistryWebApplication.GSON;
		int id = Integer.valueOf(getAttribute("id"));

        String email = gson.fromJson(payload, String.class);

        try {

			if (!getClientInfo().getUser().getIdentifier().equals(email))
                throw new UnauthorizedUserException("You are not authorized.");

			EventsAccessObject.addEventSubscriber(id, email);		
			
			return gson.toJson(true, boolean.class);
		} catch (InvalidEventIdException e) {
			Status status = new Status(ErrorCodes.INVALID_EVENT_ID);
			setStatus(status);
			
			return gson.toJson(e, InvalidEventIdException.class);
		} catch (InvalidUserEmailException e) {
			Status status = new Status(ErrorCodes.INVALID_USER_EMAIL);
			setStatus(status);
			
			return gson.toJson(e, InvalidUserEmailException.class);
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
	
	@Delete("json")
	public String deleteSubscriber(String payload) {
		Gson gson = EventsRegistryWebApplication.GSON;
		int id = Integer.valueOf(getAttribute("id"));
		
		String email = gson.fromJson(payload, String.class);
		
		try {
			if (email == null)
				throw new VoidClassFieldException("You are not authorized. You have to specify the subscriber email in the URI.");
			
			if (!getClientInfo().getUser().getIdentifier().equals(email))
				throw new UnauthorizedUserException("You are not authorized.");
			
			EventsAccessObject.removeEventSubscriber(id, email);
			
			return gson.toJson(true, boolean.class);
		} catch (InvalidEventIdException e) {
			Status status = new Status(ErrorCodes.INVALID_EVENT_ID);
			setStatus(status);
			
			return gson.toJson(e, InvalidEventIdException.class);
		} catch (InvalidUserEmailException e) {
			Status status = new Status(ErrorCodes.INVALID_USER_EMAIL);
			setStatus(status);
			
			return gson.toJson(e, InvalidUserEmailException.class);
		} catch (InvalidParticipationException e) {
			Status status = new Status(ErrorCodes.INVALID_PARTICIPATION);
			setStatus(status);
			
			return gson.toJson(e, InvalidParticipationException.class);
		}  catch (UnauthorizedUserException e) {
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
