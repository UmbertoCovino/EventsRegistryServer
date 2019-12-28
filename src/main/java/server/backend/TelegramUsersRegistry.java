package server.backend;

import java.util.TreeMap;

import commons.exceptions.InvalidUserEmailException;
import commons.exceptions.InvalidUserTokenException;
import commons.User;

public class TelegramUsersRegistry {
	private TreeMap<Integer, User> token_map;
	private TreeMap<String, Long> chat_id_map;	// key: user_email, object: chat_id
	private int token;
	private static TelegramUsersRegistry instance;

	private TelegramUsersRegistry() {
		token_map = new TreeMap<Integer, User>();
		chat_id_map = new TreeMap<String, Long>();
		token = 0; //initialize with file_log
	}
	
	public static synchronized TelegramUsersRegistry instance() {
		if (instance == null)
			instance = new TelegramUsersRegistry();
		
		return instance;
	}
	
	public synchronized int getNextAvailableToken() {
		return ++token;
	}
	
	public synchronized int sizeTokenMap() {
		return token_map.size();
	}
	
	public synchronized User getUserByToken(Integer token) {
		User user = token_map.get(token);

		if (user != null)
			return user;
		else return null;
	}

	public synchronized void addUserByToken(int token, User user) throws InvalidUserTokenException {
		if (token_map.containsKey(token)) 
			throw new InvalidUserTokenException("Duplicated token: " + token);
		
		token_map.put(token, user);
	}

	public synchronized void removeUserByToken(int token) throws InvalidUserTokenException {
		if (!token_map.containsKey(token)) 
			throw new InvalidUserTokenException("Invalid user token: " + token);
		
		token_map.remove(token);			
	}
	
	///////////////////////////////////////////// chat_id_map management /////////////////////////////////////////////////
	
	public synchronized int sizeChatIdMap() {
		return token_map.size();
	}
	
	public synchronized Long getChatIdByEmail(String email) {
		Long chat_id = chat_id_map.get(email);

		if (chat_id != null)
			return chat_id;
		else return null;
	}
	
	public synchronized void addChatIdByEmail(String email, Long chat_id) throws InvalidUserEmailException {
		if (chat_id_map.containsKey(email)) 
			throw new InvalidUserEmailException("Duplicated email: " + email);
		
		chat_id_map.put(email, chat_id);
	}
	
	public synchronized void removeChatIdByEmail(String email) throws InvalidUserEmailException {
		if (!chat_id_map.containsKey(email)) 
			throw new InvalidUserEmailException("Invalid user email: " + email);
		
		chat_id_map.remove(email);			
	}
}