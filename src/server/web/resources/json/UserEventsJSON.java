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
import commons.InvalidEventIdException;
import commons.InvalidUserEmailException;
import commons.UnauthorizedUserException;
import commons.User;
import server.backend.wrapper.UsersRegistryAPI;

public class UserEventsJSON extends ServerResource {
	
	@Get
	public String getEvents() throws ParseException, JsonSyntaxException, InvalidEventIdException {   	
		Gson gson = new Gson();
		UsersRegistryAPI urapi = UsersRegistryAPI.instance();
		
		try {
			if (!getClientInfo().getUser().getIdentifier().equals(getAttribute("email")))
				throw new UnauthorizedUserException("You are not authorized.");
			
			User user = urapi.get(getAttribute("email"));
			
			Event[] events = gson.fromJson(new EventsRegistryJSON().getEvents(), Event[].class);
			ArrayList<Event> eventsFilteredAL = new ArrayList<Event>();
			
			for (Event event: events)
				if (event.getUserEmail().equals(user.getEmail()))
					eventsFilteredAL.add(event);
			
			Event[] eventsFiltered = eventsFilteredAL.toArray(new Event[eventsFilteredAL.size()]);
			
			return gson.toJson(eventsFiltered, Event[].class);
		} catch (InvalidUserEmailException e) {
			Status status = new Status(ErrorCodes.INVALID_USER_EMAIL);
			setStatus(status);
			
			return gson.toJson(e, InvalidUserEmailException.class);
		} catch (UnauthorizedUserException e) {
			Status status = new Status(ErrorCodes.UNAUTHORIZED_USER);
			setStatus(status);
			
			return gson.toJson(e, UnauthorizedUserException.class);
		}
	}
}
