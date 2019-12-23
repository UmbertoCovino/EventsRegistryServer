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
import commons.exceptions.InvalidUserEmailException;
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
		} catch (GenericSQLException e) {
			Status status = new Status(ErrorCodes.GENERIC_SQL);
			setStatus(status);
			
			return gson.toJson(e, GenericSQLException.class);
		}
		
		return gson.toJson(subscribers.toArray(new User[subscribers.size()]), User[].class);
	}
	
	@Post("json")
	public String addSubscriber(String payload) {
		Gson gson = EventsRegistryWebApplication.GSON;
		
		String email = gson.fromJson(payload, String.class);
		try {
			EventsAccessObject.addEventSubscriber(Integer.valueOf(getAttribute("id")), email);		
			
			return gson.toJson(true, boolean.class);
		} catch (InvalidEventIdException e) {
			Status status = new Status(ErrorCodes.INVALID_EVENT_ID);
			setStatus(status);
			
			return gson.toJson(e, InvalidEventIdException.class);
		} catch (InvalidUserEmailException e) {
			Status status = new Status(ErrorCodes.INVALID_USER_EMAIL);
			setStatus(status);
			
			return gson.toJson(e, InvalidUserEmailException.class);
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
		
		String email = gson.fromJson(payload, String.class);
		try {
			EventsAccessObject.removeEventSubscriber(Integer.valueOf(getAttribute("id")), email);		
			
			return gson.toJson(true, boolean.class);
		} catch (InvalidEventIdException e) {
			Status status = new Status(ErrorCodes.INVALID_EVENT_ID);
			setStatus(status);
			
			return gson.toJson(e, InvalidEventIdException.class);
		} catch (InvalidUserEmailException e) {
			Status status = new Status(ErrorCodes.INVALID_USER_EMAIL);
			setStatus(status);
			
			return gson.toJson(e, InvalidUserEmailException.class);
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
