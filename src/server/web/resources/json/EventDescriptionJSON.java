package server.web.resources.json;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;

import commons.ErrorCodes;
import commons.Event;
import commons.InvalidEventIdException;
import commons.UnauthorizedUserException;
import server.backend.wrapper.EventsRegistryAPI;

public class EventDescriptionJSON extends ServerResource {
	
	@Get
    public String getDescription() {
		Gson gson = new Gson();
		EventsRegistryAPI erapi = EventsRegistryAPI.instance();
		
		try {
			Event event = erapi.get(getAttribute("id"));
			
			return gson.toJson(event.getDescription(), String.class);   	
		} catch (InvalidEventIdException e) {
			Status status = new Status(ErrorCodes.INVALID_EVENT_ID);
			setStatus(status);
			
			return gson.toJson(e, InvalidEventIdException.class);
		}
    }
    
    @Put
    public String updateDescription(String payload) {
		Gson gson = new Gson();
		EventsRegistryAPI erapi = EventsRegistryAPI.instance();
		
		try {
			Event event = erapi.get(getAttribute("id"));
			
			if (!getClientInfo().getUser().getIdentifier().equals(event.getUserEmail()))
				throw new UnauthorizedUserException("You are not authorized.");
			
			event.setDescription(gson.fromJson(payload, String.class));
			erapi.update(event);
			
			return gson.toJson("Description updated for event with id " + getAttribute("id") + ".", String.class);
		} catch (InvalidEventIdException e) {
			Status status = new Status(ErrorCodes.INVALID_EVENT_ID);
			setStatus(status);
			
			return gson.toJson(e, InvalidEventIdException.class);
		} catch (UnauthorizedUserException e) {
			Status status = new Status(ErrorCodes.UNAUTHORIZED_USER);
			setStatus(status);
			
			return gson.toJson(e, UnauthorizedUserException.class);
		}
	}
}
