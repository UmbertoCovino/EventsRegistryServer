package server.web.resources.json;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.UUID;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;

import commons.Event;
import commons.ErrorCodes;
import commons.InvalidEventIdException;
import commons.InvalidUserEmailException;
import commons.UnauthorizedUserException;
import server.backend.wrapper.EventsRegistryAPI;
import server.backend.wrapper.UsersRegistryAPI;

public class EventsRegistryJSON extends ServerResource {
	
	@Get
	public String getEvents() throws ParseException, InvalidEventIdException, InvalidUserEmailException {   	
		Gson gson = new Gson();
		EventsRegistryAPI erapi = EventsRegistryAPI.instance();
		
		String[] ids = erapi.ids();
		Event[] events = new Event[ids.length];
		
		for (int i = 0; i < ids.length; i++) {
			events[i] = erapi.get(ids[i]).clone();
			events[i].setUser(UsersRegistryAPI.instance().get(events[i].getUserEmail()).cloneWithoutPassword());
		}
		
		Arrays.sort(events, new Comparator<Event>() {
			@Override
			public int compare(Event o1, Event o2) {
				return o1.getDate().compareTo(o2.getDate());
			}
	    });
		
		return gson.toJson(events, Event[].class);
	}
    
    @Post
    public String addEvent(String payload) throws ParseException, InvalidUserEmailException {   	
		Gson gson = new Gson();
		EventsRegistryAPI erapi = EventsRegistryAPI.instance();
		
		Event event = gson.fromJson(payload, Event.class);
		try {
			String uniqueID = UUID.randomUUID().toString();
			event.setId(uniqueID);
			event.setPhoto(uniqueID + ".jpg");
			event.setUserEmail(getClientInfo().getUser().getIdentifier());
			
			event.setDate(Event.DATETIME_SIMPLE_DATE_FORMAT.parse(Event.DATE_SIMPLE_DATE_FORMAT.format(event.getDate()) + "-" + Event.TIME_SIMPLE_DATE_FORMAT.format(event.getStartTime())));
			
			erapi.add(event);
			
//			return gson.toJson("Event with id " + event.getId() + " added.", String.class);
			return gson.toJson(event.getId(), String.class);
//			return gson.toJson(event, Event.class);
		} catch (InvalidEventIdException e) {    		
			Status status = new Status(ErrorCodes.INVALID_EVENT_ID);
			setStatus(status);
			
			return gson.toJson(e, InvalidEventIdException.class);
		}    		
    }
    
    @Put
    public String updateEvent(String payload) throws ParseException, InvalidEventIdException {
		Gson gson = new Gson();
		EventsRegistryAPI erapi = EventsRegistryAPI.instance();
		
		Event event = gson.fromJson(payload, Event.class);
		try {
			if (!getClientInfo().getUser().getIdentifier().equals(event.getUserEmail()))
				throw new UnauthorizedUserException("You are not authorized.");
			
			erapi.update(event);
			
			return gson.toJson(true, boolean.class);
//			return gson.toJson("Event with id " + event.getId() + " updated.", String.class);
		} catch (UnauthorizedUserException e) {
			Status status = new Status(ErrorCodes.UNAUTHORIZED_USER);
			setStatus(status);
			
			return gson.toJson(e, UnauthorizedUserException.class);
		}
    }
    
//    @Delete
//    public String deleteAll() {
//		//to be implemented
//		
//		return null;
//    }
}
