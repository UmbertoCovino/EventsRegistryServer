package server.backend.wrapper;

import java.io.File;
import java.io.IOException;

import commons.Event;
import commons.InvalidEventIdException;
import server.backend.EventsRegistry;

public class EventsRegistryAPI {
	private static EventsRegistryAPI instance;
	
	private EventsRegistry er;
	private String storageDirectory;
	private String storageFileName;
	private String photosDirectory;
	
	protected EventsRegistryAPI() {
		er = new EventsRegistry();
	}

	public static synchronized EventsRegistryAPI instance() {
		if (instance == null)
			instance = new EventsRegistryAPI();
		
		return instance;
	}
	
	public synchronized int size() {
		return er.size();
	}

	public synchronized Event get(String id) throws InvalidEventIdException {
		return er.get(id); 
	}

	public synchronized String[] ids() {
		return er.ids();
	}

	public synchronized void add(Event event) throws InvalidEventIdException {
		er.add(event);
		commit();
	}

	public synchronized void update(Event event) throws InvalidEventIdException {
		er.update(event);
		commit();
	}

	public synchronized void remove(String id) throws InvalidEventIdException {
		er.remove(id);
		commit();
	}
	
	
	
	public String getPhotosDirectory() {
		return photosDirectory;
	}
	
	

	public void setStorageFiles(String storageDirectory, String storageFileName, String photosDirectory) {
		this.storageDirectory = storageDirectory;
		this.storageFileName = storageFileName;
		this.photosDirectory = photosDirectory;
		
		System.err.println("Events storage directory: " + this.storageDirectory);
		System.err.println("Events storage file name: " + this.storageFileName);
		System.err.println("Events photos directory: " + this.photosDirectory);
	}
	
	public void commit() {
		String fileName = storageDirectory + storageFileName;
		
		System.err.println("Commit events storage to: " + fileName);
		
		try {
			er.save(fileName);
		} catch (IOException e) {
			System.err.println("Commit events failed!");
			e.printStackTrace();
		}		
	}
	
	public void restore() {
		String fileName = storageDirectory + storageFileName;
		
		File file = new File(fileName);
		
		if (!file.exists()) {
			System.err.println("No events to load; starting a new registry.");
		} else {
			System.err.println("Restore events storage from: " + fileName);
			
			try {
				er.load(fileName);
			} catch (ClassNotFoundException | IOException e) {
				System.err.println("Restore events failed; starting a new registry.");
				er = new EventsRegistry();
			}
		}
	}
}


