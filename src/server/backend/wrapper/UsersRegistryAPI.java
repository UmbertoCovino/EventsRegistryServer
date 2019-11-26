package server.backend.wrapper;

import java.io.File;
import java.io.IOException;

import commons.InvalidUserEmailException;
import commons.User;
import server.backend.UsersRegistry;

public class UsersRegistryAPI {
	private static UsersRegistryAPI instance;
	
	private UsersRegistry ur;
	private String storageDirectory;
	private String storageFileName;
	private String photosDirectory;
	
	protected UsersRegistryAPI() {
		ur = new UsersRegistry();
	}

	public static synchronized UsersRegistryAPI instance() {
		if (instance == null)
			instance = new UsersRegistryAPI();
		
		return instance;
	}
	
	public synchronized int size() {
		return ur.size();
	}

	public synchronized User get(String email) throws InvalidUserEmailException {
		return ur.get(email); 
	}

	public synchronized String[] emails() {
		return ur.emails();
	}

	public synchronized void add(User user) throws InvalidUserEmailException {
		ur.add(user);
		commit();
	}

	public synchronized void update(User user) throws InvalidUserEmailException {
		ur.update(user);
		commit();
	}

	public synchronized void remove(String email) throws InvalidUserEmailException {
		ur.remove(email);
		commit();
	}
	
	
	
	public String getPhotosDirectory() {
		return photosDirectory;
	}
	
	
	
	public void setStorageFiles(String storageDirectory, String storageFileName, String photosDirectory) {
		this.storageDirectory = storageDirectory;
		this.storageFileName = storageFileName;
		this.photosDirectory = photosDirectory;
		
		System.err.println("Users storage directory: " + this.storageDirectory);
		System.err.println("Users storage file name: " + this.storageFileName);
		System.err.println("Users photos directory: " + this.photosDirectory);	
	}
	
	public void commit() {
		String fileName = storageDirectory + storageFileName;
		
		System.err.println("Commit users storage to: " + fileName);
		
		try {
			ur.save(fileName);
		} catch (IOException e) {
			System.err.println("Commit users failed!");
		}		
	}
	
	public void restore() {
		String fileName = storageDirectory + storageFileName;
		
		File file = new File(fileName);
		
		if (!file.exists()) {
			System.err.println("No users to load; starting a new registry.");
		} else {
			System.err.println("Restore users storage from: " + fileName);
			
			try {
				ur.load(fileName);
			} catch (ClassNotFoundException | IOException e) {
				System.err.println("Restore users failed; starting a new registry.");
				ur = new UsersRegistry();
			}
		}
	}
}


