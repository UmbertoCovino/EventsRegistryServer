package server.backend;

import java.util.TreeMap;

import commons.InvalidUserEmailException;
import commons.User;

public class TelegramUsersRegistry {
	private TreeMap<String, Integer> users;
	private int token;
	private static TelegramUsersRegistry instance;

	private TelegramUsersRegistry() {
		users = new TreeMap<String, Integer>();
		token = 0; //initialize with file_log
	}
	
	public static synchronized TelegramUsersRegistry instance() {
		if (instance == null)
			instance = new TelegramUsersRegistry();
		
		return instance;
	}
	
	public synchronized int size() {
		return users.size();
	}

	public synchronized Integer get(String email) {
		Integer token = users.get(email);

		if (token != null)
			return token;
		else return null;
	}
	
	public synchronized int getNextAvailableToken() {
		return ++token;
	}

	public synchronized void add(User user, int token) throws InvalidUserEmailException {
		if (users.containsKey(user.getEmail())) 
			throw new InvalidUserEmailException("Duplicated email: " + user.getEmail());
		
		users.put(user.getEmail(), token);
	}

	public synchronized void remove(String email) throws InvalidUserEmailException {
		if (!users.containsKey(email)) 
			throw new InvalidUserEmailException("Invalid user email: " + email);
		
		users.remove(email);			
	}
}
