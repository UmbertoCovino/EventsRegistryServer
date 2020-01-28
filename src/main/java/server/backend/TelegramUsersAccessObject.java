package server.backend;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeMap;

import commons.exceptions.GenericSQLException;
import commons.exceptions.InvalidUserEmailException;
import commons.exceptions.InvalidUserTokenException;
import commons.exceptions.VoidClassFieldException;
import commons.User;

public class TelegramUsersAccessObject {
	private TreeMap<Integer, User> token_map;
	private TreeMap<String, Long> chat_id_map;	// key: user_email, object: chat_id
	private int token;
	private static TelegramUsersAccessObject instance;
	
	private TelegramUsersAccessObject() {
		token_map = new TreeMap<Integer, User>();
		chat_id_map = new TreeMap<String, Long>();
		token = 0; //initialize with file_log
	}
	
	public static synchronized TelegramUsersAccessObject instance() {
		if (instance == null)
			instance = new TelegramUsersAccessObject();
		
		return instance;
	}
	
	public synchronized static long getUserChatId(String email) throws InvalidUserEmailException, GenericSQLException, VoidClassFieldException {
		long chat_id;

		if (email == null || email.equals(""))
			throw new VoidClassFieldException("The email passed cannot be null or empty.");
		
		try {
			ResultSet rs = DBManager.executeQuery("select chat_id from users where email = '" + email + "';");

			if (rs.next()) {
				chat_id = rs.getLong("chat_id");
			} else
				throw new InvalidUserEmailException("Inexistent user email: " + email);
			
			rs.close();
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		return chat_id;
	}
	
	public synchronized static int updateUserChatId(String email, long chat_id) throws InvalidUserEmailException, GenericSQLException, VoidClassFieldException {
		int result = 0;
		
		if (email == null || email.equals(""))
			throw new VoidClassFieldException("The email passed cannot be null or empty.");
		
		try {
			ResultSet rs = DBManager.executeQuery("select count(*) as users_number from users where email = '" + email + "';");
			
			if (rs.next()) {
				if (rs.getInt("users_number") == 0)
					throw new InvalidUserEmailException("Inexistent user email: " + email);
				
				result = DBManager.executeUpdate("update users "
											   + "set chat_id = '" + chat_id + "' "
											   + "where email = '" + email + "';");
			}
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		if (result == 1)
			return 1;
		else
			throw new GenericSQLException("An error occurred while updating user to DB.");
	}
	
	///////////////////////////////////////////// tokens_map management /////////////////////////////////////////////////
	
	public synchronized int getNextAvailableToken() {
		return ++token;
	}
	
//	public synchronized int sizeTokenMap() {
//		return token_map.size();
//	}
	
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

//	public synchronized void removeUserByToken(int token) throws InvalidUserTokenException {
//		if (!token_map.containsKey(token))
//			throw new InvalidUserTokenException("Invalid user token: " + token);
//
//		token_map.remove(token);
//	}
//
//	///////////////////////////////////////////// chat_id_map management /////////////////////////////////////////////////
//
//	public synchronized int sizeChatIdMap() {
//		return token_map.size();
//	}
//
//	public synchronized Long getChatIdByEmail(String email) {
//		Long chat_id = chat_id_map.get(email);
//
//		if (chat_id != null)
//			return chat_id;
//		else return (long) 0;
//	}
//
//	public synchronized void addChatIdByEmail(String email, Long chat_id) throws InvalidUserEmailException {
//		if (chat_id_map.containsKey(email))
//			throw new InvalidUserEmailException("Duplicated email: " + email);
//
//		chat_id_map.put(email, chat_id);
//	}
//
//	public synchronized void removeChatIdByEmail(String email) throws InvalidUserEmailException {
//		if (!chat_id_map.containsKey(email))
//			throw new InvalidUserEmailException("Invalid user email: " + email);
//
//		chat_id_map.remove(email);
//	}
}