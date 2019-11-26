package server.backend;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.util.TreeMap;

import commons.InvalidEventIdException;
import commons.Event;

public class EventsRegistry {
	private TreeMap<String, Event> events;

	public EventsRegistry() {
		events = new TreeMap<String, Event>();
	}

	public int size() {
		return events.size();
	}

	public Event get(String id) throws InvalidEventIdException {
		Event event = events.get(id);

		if (event != null)
			return event;

		throw new InvalidEventIdException("Invalid event id: " + id);
	}

	public String[] ids() {
		return events.keySet().toArray(new String[events.keySet().size()]);
	}

	public void add(Event event) throws InvalidEventIdException {
		if (events.containsKey(event.getId())) 
			throw new InvalidEventIdException("Duplicated event id: " + event.getId());
		
		events.put(event.getId(), event);
	}

	public void update(Event newEvent) throws InvalidEventIdException {
		if (!events.containsKey(newEvent.getId())) 
			throw new InvalidEventIdException("Invalid event id: " + newEvent.getId());
		
		try {
			Event oldEvent = events.get(newEvent.getId());
			oldEvent.setTitle(newEvent.getTitle());
			oldEvent.setDate(Event.DATETIME_SIMPLE_DATE_FORMAT.parse(Event.DATE_SIMPLE_DATE_FORMAT.format(newEvent.getDate()) + "-" + Event.TIME_SIMPLE_DATE_FORMAT.format(newEvent.getStartTime())));
			oldEvent.setStartTime(newEvent.getStartTime());
			oldEvent.setEndTime(newEvent.getEndTime());
			oldEvent.setDescription(newEvent.getDescription());
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void remove(String id) throws InvalidEventIdException {
		if (!events.containsKey(id)) 
			throw new InvalidEventIdException("Invalid event id: " + id);
		
		events.remove(id);			
	}

	public void save(String fileName) throws IOException {		
		FileOutputStream fos = new FileOutputStream(fileName);
		ObjectOutputStream out = new ObjectOutputStream(fos);
		
		out.writeObject(events);
		
		out.close();
		fos.close();
	}

	@SuppressWarnings("unchecked")
	public void load(String fileName) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(fileName);
		ObjectInputStream in = new ObjectInputStream(fis);
		
		events = (TreeMap<String, Event>) in.readObject();
		
		in.close();
		fis.close();
	}
}
