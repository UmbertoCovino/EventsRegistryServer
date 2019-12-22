package server.web.resources.json;

import java.text.ParseException;
import java.util.ArrayList;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import commons.ErrorCodes;
import commons.Event;
import commons.GenericSQLException;
import commons.InvalidEventIdException;
import commons.InvalidUserEmailException;
import commons.UnauthorizedUserException;
import server.backend.UsersAccessObject;

public class UserEventsJSON extends ServerResource {
	
	@Get
	public String getEvents() throws ParseException, JsonSyntaxException, InvalidEventIdException {   	
		Gson gson = new Gson();
		String email = getAttribute("email");
		
		try {
			if (!getClientInfo().getUser().getIdentifier().equals(email))
				throw new UnauthorizedUserException("You are not authorized.");
			
			ArrayList<Event> events = UsersAccessObject.getUserEvents(getAttribute("email"));
			
			return gson.toJson(events.toArray(new Event[events.size()]), Event[].class);
		} catch (InvalidUserEmailException e) {
			Status status = new Status(ErrorCodes.INVALID_USER_EMAIL);
			setStatus(status);
			
			return gson.toJson(e, InvalidUserEmailException.class);
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
