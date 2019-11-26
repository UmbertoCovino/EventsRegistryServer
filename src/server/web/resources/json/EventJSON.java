package server.web.resources.json;

import java.io.File;
import java.text.ParseException;

import org.restlet.data.Status;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;

import commons.Event;
import commons.ErrorCodes;
import commons.InvalidEventIdException;
import commons.InvalidUserEmailException;
import commons.UnauthorizedUserException;
import server.backend.wrapper.EventsRegistryAPI;
import server.backend.wrapper.UsersRegistryAPI;

public class EventJSON extends ServerResource {
	
    @Get
    public String getEvent() throws ParseException, InvalidUserEmailException {   	
		Gson gson = new Gson();
		EventsRegistryAPI erapi = EventsRegistryAPI.instance();
		
		try {
			Event event = erapi.get(getAttribute("id")).clone();
			event.setUser(UsersRegistryAPI.instance().get(event.getUserEmail()).cloneWithoutPassword());
			
			return gson.toJson(event, Event.class);   	
		} catch (InvalidEventIdException e) {
			Status status = new Status(ErrorCodes.INVALID_EVENT_ID);
			setStatus(status);
			
			return gson.toJson(e, InvalidEventIdException.class);
		}
    }
    
    @Delete
    public String deleteEvent(){
    	Gson gson = new Gson();
		EventsRegistryAPI erapi = EventsRegistryAPI.instance();
		
		try {
			Event event = erapi.get(getAttribute("id"));
			
			if (!getClientInfo().getUser().getIdentifier().equals(event.getUserEmail()))
				throw new UnauthorizedUserException("You are not authorized.");
			
			new File(erapi.getPhotosDirectory() + "/" + event.getPhoto()).delete();
			
			erapi.remove(getAttribute("id"));
			
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
		}
    }
}
