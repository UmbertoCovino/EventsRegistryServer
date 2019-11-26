package server.web.resources.json;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;

import commons.Event;
import commons.InvalidEventIdException;
import commons.InvalidUserEmailException;
import server.backend.wrapper.EventsRegistryAPI;
import server.backend.wrapper.UsersRegistryAPI;

public class EventsRegistryEventsFromDateToDateJSON extends ServerResource {
	
	@Get
	public String getEvents() throws ParseException, InvalidEventIdException, InvalidUserEmailException {   	
		Gson gson = new Gson();
		EventsRegistryAPI erapi = EventsRegistryAPI.instance();
		
		String[] ids = erapi.ids();
		ArrayList<Event> events = new ArrayList<Event>();
		
		Date fromDate = Event.DATETIME_SIMPLE_DATE_FORMAT.parse(getAttribute("from"));
		Date toDate = Event.DATETIME_SIMPLE_DATE_FORMAT.parse(getAttribute("to"));
		
		for (int i = 0; i < ids.length; i++) {
			Event event = erapi.get(ids[i]).clone();
			
			Date eventStartDate = Event.DATETIME_SIMPLE_DATE_FORMAT.parse(Event.DATE_SIMPLE_DATE_FORMAT.format(event.getDate()) + "-" + Event.TIME_SIMPLE_DATE_FORMAT.format(event.getStartTime()));
			Date eventEndDate = Event.DATETIME_SIMPLE_DATE_FORMAT.parse(Event.DATE_SIMPLE_DATE_FORMAT.format(event.getDate()) + "-" + Event.TIME_SIMPLE_DATE_FORMAT.format(event.getEndTime()));
			
			if ((eventEndDate.after(fromDate) || eventEndDate.equals(fromDate)) && (eventStartDate.before(toDate) || eventStartDate.equals(toDate))) {
				events.add(event);
				event.setUser(UsersRegistryAPI.instance().get(event.getUserEmail()).cloneWithoutPassword());
			}
		}
		
		Event[] events1 = events.toArray(new Event[events.size()]);
		
		Arrays.sort(events1, new Comparator<Event>() {
			@Override
			public int compare(Event o1, Event o2) {
				return o1.getDate().compareTo(o2.getDate());
			}
	    });
		
		return gson.toJson(events1, Event[].class);
	}
}
