package server.backend;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import commons.Event;
import commons.User;
import commons.exceptions.GenericSQLException;
import commons.exceptions.InvalidUserEmailException;
import commons.exceptions.VoidClassFieldException;

public class UsersAccessObject {
	
	
	public static boolean isEmailValid(String email) {
		String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";

		return email.matches(regex);
	}
	
	
	
	// GETTERS ----------------------------------------------------------------------------------------------

	public synchronized static int getNumberOfUsers() throws GenericSQLException {
		Integer usersNumber = null;
		
		try {
			ResultSet rs = DBManager.executeQuery("select count(*) as users_number from users;");
			
			if (rs.next())
				usersNumber = rs.getInt("users_number");
			
			rs.close();
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		return usersNumber;
	}
	
	public synchronized static User getUser(String email) throws InvalidUserEmailException, GenericSQLException, VoidClassFieldException {
		User user = null;
		
		if (email == null || email.equals(""))
			throw new VoidClassFieldException("The email passed cannot be null or empty.");
		else if (email.length() > 80)
			throw new VoidClassFieldException("The email passed cannot be over 80 chars.");
		else if (!isEmailValid(email))
			throw new VoidClassFieldException("The email passed is not valid.");
		
		try {
			ResultSet rs = DBManager.executeQuery("select * from users where email = '" + email + "';");

			if (rs.next()) {
				String name = rs.getString("name");
				String surname = rs.getString("surname");
				String photoPath = rs.getString("photo_path");
							
				user = new User(name, surname, email, photoPath);
			} else
				throw new InvalidUserEmailException("Inexistent user email: " + email);
			
			rs.close();
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		return user;
	}
	
	public synchronized static User getUserWithPassword(String email) throws InvalidUserEmailException, GenericSQLException, VoidClassFieldException {
		User user = null;
		
		if (email == null || email.equals(""))
			throw new VoidClassFieldException("The email passed cannot be null or empty.");
		else if (email.length() > 80)
			throw new VoidClassFieldException("The email passed cannot be over 80 chars.");
		else if (!isEmailValid(email))
			throw new VoidClassFieldException("The email passed is not valid.");
		
		try {
			ResultSet rs = DBManager.executeQuery("select * from users where email = '" + email + "';");

			if (rs.next()) {
				String name = rs.getString("name");
				String surname = rs.getString("surname");
				String password = rs.getString("password");
				String photoPath = rs.getString("photo_path");
				
				user = new User(name, surname, email, password, photoPath);
			} else
				throw new InvalidUserEmailException("Inexistent user email: " + email);
			
			rs.close();
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		return user;
	}
	
	public synchronized static String getUserName(String email) throws InvalidUserEmailException, GenericSQLException, VoidClassFieldException {
		String name = null;

		if (email == null || email.equals(""))
			throw new VoidClassFieldException("The email passed cannot be null or empty.");
		else if (email.length() > 80)
			throw new VoidClassFieldException("The email passed cannot be over 80 chars.");
		else if (!isEmailValid(email))
			throw new VoidClassFieldException("The email passed is not valid.");
		
		try {
			ResultSet rs = DBManager.executeQuery("select name from users where email = '" + email + "';");

			if (rs.next()) {
				name = rs.getString("name");
			} else
				throw new InvalidUserEmailException("Inexistent user email: " + email);
			
			rs.close();
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		return name;
	}
	
	public synchronized static String getUserSurname(String email) throws InvalidUserEmailException, GenericSQLException, VoidClassFieldException {
		String surname = null;

		if (email == null || email.equals(""))
			throw new VoidClassFieldException("The email passed cannot be null or empty.");
		else if (email.length() > 80)
			throw new VoidClassFieldException("The email passed cannot be over 80 chars.");
		else if (!isEmailValid(email))
			throw new VoidClassFieldException("The email passed is not valid.");
		
		try {
			ResultSet rs = DBManager.executeQuery("select surname from users where email = '" + email + "';");

			if (rs.next()) {
				surname = rs.getString("surname");
			} else
				throw new InvalidUserEmailException("Inexistent user email: " + email);
			
			rs.close();
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		return surname;
	}
	
	public synchronized static String getUserPassword(String email) throws InvalidUserEmailException, GenericSQLException, VoidClassFieldException {
		String password = null;

		if (email == null || email.equals(""))
			throw new VoidClassFieldException("The email passed cannot be null or empty.");
		else if (email.length() > 80)
			throw new VoidClassFieldException("The email passed cannot be over 80 chars.");
		else if (!isEmailValid(email))
			throw new VoidClassFieldException("The email passed is not valid.");
		
		try {
			ResultSet rs = DBManager.executeQuery("select password from users where email = '" + email + "';");

			if (rs.next()) {
				password = rs.getString("password");
			} else
				throw new InvalidUserEmailException("Inexistent user email: " + email);
			
			rs.close();
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		return password;
	}
	
	public synchronized static String getUserPhotoPath(String email) throws InvalidUserEmailException, GenericSQLException, VoidClassFieldException {
		String photoPath = null;

		if (email == null || email.equals(""))
			throw new VoidClassFieldException("The email passed cannot be null or empty.");
		else if (email.length() > 80)
			throw new VoidClassFieldException("The email passed cannot be over 80 chars.");
		else if (!isEmailValid(email))
			throw new VoidClassFieldException("The email passed is not valid.");
		
		try {
			ResultSet rs = DBManager.executeQuery("select photo_path from users where email = '" + email + "';");

			if (rs.next()) {
				photoPath = rs.getString("photo_path");
			} else
				throw new InvalidUserEmailException("Inexistent user email: " + email);
			
			rs.close();
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		return photoPath;
	}
	
	public synchronized static ArrayList<Event> getUserEvents(String email) throws InvalidUserEmailException, GenericSQLException, VoidClassFieldException {
		ArrayList<Event> events = new ArrayList<>();

		if (email == null || email.equals(""))
			throw new VoidClassFieldException("The email passed cannot be null or empty.");
		else if (email.length() > 80)
			throw new VoidClassFieldException("The email passed cannot be over 80 chars.");
		else if (!isEmailValid(email))
			throw new VoidClassFieldException("The email passed is not valid.");
		
		User user = getUser(email);
		
		try {
			ResultSet rs = DBManager.executeQuery("select * "
											   + "from events "
											   + "where user_owner_email = '" + email + "' "
											   + "order by start_date;");
			
			while (rs.next()) {
				int id = rs.getInt("id");
				String title = rs.getString("title");
				Date startDate = new Date(rs.getTimestamp("start_date").getTime());
				Date endDate = new Date(rs.getTimestamp("end_date").getTime());
				String description = rs.getString("description");
				String photoPath = rs.getString("photo_path");
				String ownerEmail = rs.getString("user_owner_email");
				
				Event event = new Event(id, title, startDate, endDate, description, photoPath, ownerEmail, user);
				
				events.add(event);
			}
			
			rs.close();
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		return events;
	}
	
	public synchronized static ArrayList<User> getUsers() throws GenericSQLException {
		ArrayList<User> users = new ArrayList<>();
		
		try {
			ResultSet rs = DBManager.executeQuery("select * from users;");
			
			while (rs.next()) {
				String email = rs.getString("email");
				String name = rs.getString("name");
				String surname = rs.getString("surname");
				String photoPath = rs.getString("photo_path");
				
				User user = new User(name, surname, email, photoPath);
				
				users.add(user);
			}
			
			rs.close();
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		return users;
	}

	public synchronized static String[] getUsersEmails() throws GenericSQLException {
		ArrayList<String> emails = new ArrayList<>();
		
		try {
			ResultSet rs = DBManager.executeQuery("select email from users;");
			
			while (rs.next()) {
				String email = rs.getString("email");
				
				emails.add(email);
			}
			
			rs.close();
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}

		return emails.toArray(new String[emails.size()]);
	}
	
	// ADDERS -----------------------------------------------------------------------------------------------

	public synchronized static void addUser(User user) throws InvalidUserEmailException, GenericSQLException, VoidClassFieldException {
		int result = 0;

		if (user.getEmail() == null || user.getEmail().equals(""))
			throw new VoidClassFieldException("The email passed cannot be null or empty.");
		else if (user.getEmail().length() > 80)
			throw new VoidClassFieldException("The email passed cannot be over 80 chars.");
		else if (!isEmailValid(user.getEmail()))
			throw new VoidClassFieldException("The email passed is not valid.");
		else if (user.getName() == null || user.getName().equals(""))
			throw new VoidClassFieldException("The name passed cannot be null or empty.");
		else if (user.getName().length() > 80)
			throw new VoidClassFieldException("The name passed cannot be over 80 chars.");
		else if (user.getSurname() == null || user.getSurname().equals(""))
			throw new VoidClassFieldException("The surname passed cannot be null or empty.");
		else if (user.getSurname().length() > 80)
			throw new VoidClassFieldException("The surname passed cannot be over 80 chars.");
		else if (user.getPassword() == null || user.getPassword().equals(""))
			throw new VoidClassFieldException("The password passed cannot be null or empty.");
		else if (user.getPassword().length() > 20)
			throw new VoidClassFieldException("The password passed cannot be over 20 chars.");
		else if (user.getPhotoPath() == null || user.getPhotoPath().equals(""))
			throw new VoidClassFieldException("The photo path passed cannot be null or empty.");
		else if (user.getPhotoPath().length() > 80)
			throw new VoidClassFieldException("The photo path passed cannot be over 80 chars.");
		
		try {
			ResultSet rs = DBManager.executeQuery("select count(*) as users_number from users where email = '" + user.getEmail() + "';");
			
			if (rs.next()) {
				if (rs.getInt("users_number") > 0)
					throw new InvalidUserEmailException("Duplicated user email: " + user.getEmail());
				
				result = DBManager.executeUpdate("insert into users "
											   + "values ('" + user.getName() + "', "
												       + "'" + user.getSurname() + "', "
													   + "'" + user.getEmail() + "', "
													   + "'" + user.getPassword() + "', "
													   + "'" + user.getPhotoPath() + "', "
													   + "'" + 0 + "');");
			}
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}

		if (result != 1)
			throw new GenericSQLException("An error occurred while adding user to DB.");
	}
	
	
	
	// UPDATERS --------------------------------------------------------------------------------------------

	public synchronized static int updateUser(User user) throws InvalidUserEmailException, GenericSQLException, VoidClassFieldException {
		int result = 0;
		
		if (user.getEmail() == null || user.getEmail().equals(""))
			throw new VoidClassFieldException("The email passed cannot be null or empty.");
		else if (user.getEmail().length() > 80)
			throw new VoidClassFieldException("The email passed cannot be over 80 chars.");
		else if (!isEmailValid(user.getEmail()))
			throw new VoidClassFieldException("The email passed is not valid.");
		else if (user.getName() == null || user.getName().equals(""))
			throw new VoidClassFieldException("The name passed cannot be null or empty.");
		else if (user.getName().length() > 80)
			throw new VoidClassFieldException("The name passed cannot be over 80 chars.");
		else if (user.getSurname() == null || user.getSurname().equals(""))
			throw new VoidClassFieldException("The surname passed cannot be null or empty.");
		else if (user.getSurname().length() > 80)
			throw new VoidClassFieldException("The surname passed cannot be over 80 chars.");
		else if (user.getPassword() == null || user.getPassword().equals(""))
			throw new VoidClassFieldException("The password passed cannot be null or empty.");
		else if (user.getPassword().length() > 20)
			throw new VoidClassFieldException("The password passed cannot be over 20 chars.");
		
		try {
			ResultSet rs = DBManager.executeQuery("select count(*) as users_number from users where email = '" + user.getEmail() + "';");
			
			if (rs.next()) {
				if (rs.getInt("users_number") == 0)
					throw new InvalidUserEmailException("Inexistent user email: " + user.getEmail());
				
				result = DBManager.executeUpdate("update users "
											   + "set name = '" + user.getName() + "', "
												   + "surname = '" + user.getSurname() + "' "
												   + ((user.getPassword() != null && !user.getSurname().equals("")) ? ", password = '" + user.getPassword() + "' " : "")
												// + ((user.getPhotoPath() != null && !user.getPhotoPath().equals("")) ? ", photo_path = '" + user.getPhotoPath() + "' " : "")
											   + "where email = '" + user.getEmail() + "';");
			}
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		if (result == 1)
			return 1;
		else
			throw new GenericSQLException("An error occurred while updating user to DB.");
	}
	
	public synchronized static int updateUserName(String email, String name) throws InvalidUserEmailException, GenericSQLException, VoidClassFieldException {
		int result = 0;
		
		if (email == null || email.equals(""))
			throw new VoidClassFieldException("The email passed cannot be null or empty.");
		else if (email.length() > 80)
			throw new VoidClassFieldException("The email passed cannot be over 80 chars.");
		else if (!isEmailValid(email))
			throw new VoidClassFieldException("The email passed is not valid.");
		else if (name == null || name.equals(""))
			throw new VoidClassFieldException("The name passed cannot be null or empty.");
		else if (name.length() > 80)
			throw new VoidClassFieldException("The name passed cannot be over 80 chars.");
		
		try {
			ResultSet rs = DBManager.executeQuery("select count(*) as users_number from users where email = '" + email + "';");
			
			if (rs.next()) {
				if (rs.getInt("users_number") == 0)
					throw new InvalidUserEmailException("Inexistent user email: " + email);
				
				result = DBManager.executeUpdate("update users "
											   + "set name = '" + name + "' "
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

	public synchronized static int updateUserSurname(String email, String surname) throws InvalidUserEmailException, GenericSQLException, VoidClassFieldException {
		int result = 0;

		if (email == null || email.equals(""))
			throw new VoidClassFieldException("The email passed cannot be null or empty.");
		else if (email.length() > 80)
			throw new VoidClassFieldException("The email passed cannot be over 80 chars.");
		else if (!isEmailValid(email))
			throw new VoidClassFieldException("The email passed is not valid.");
		else if (surname == null || surname.equals(""))
			throw new VoidClassFieldException("The surname passed cannot be null or empty.");
		else if (surname.length() > 80)
			throw new VoidClassFieldException("The surname passed cannot be over 80 chars.");
		
		try {
			ResultSet rs = DBManager.executeQuery("select count(*) as users_number from users where email = '" + email + "';");
			
			if (rs.next()) {
				if (rs.getInt("users_number") == 0)
					throw new InvalidUserEmailException("Inexistent user email: " + email);
				
				result = DBManager.executeUpdate("update users "
											   + "set surname = '" + surname + "' "
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

	public synchronized static int updateUserPassword(String email, String password) throws InvalidUserEmailException, GenericSQLException, VoidClassFieldException {
		int result = 0;

		if (email == null || email.equals(""))
			throw new VoidClassFieldException("The email passed cannot be null or empty.");
		else if (email.length() > 80)
			throw new VoidClassFieldException("The email passed cannot be over 80 chars.");
		else if (!isEmailValid(email))
			throw new VoidClassFieldException("The email passed is not valid.");
		else if (password == null || password.equals(""))
			throw new VoidClassFieldException("The password passed cannot be null or empty.");
		else if (password.length() > 20)
			throw new VoidClassFieldException("The password passed cannot be over 20 chars.");
		
		try {
			ResultSet rs = DBManager.executeQuery("select count(*) as users_number from users where email = '" + email + "';");
			
			if (rs.next()) {
				if (rs.getInt("users_number") == 0)
					throw new InvalidUserEmailException("Inexistent user email: " + email);
				
				result = DBManager.executeUpdate("update users "
											   + "set password = '" + password + "' "
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

	public synchronized static int updateUserPhotoPath(String email, String photoPath) throws InvalidUserEmailException, GenericSQLException, VoidClassFieldException {
		int result = 0;

		if (email == null || email.equals(""))
			throw new VoidClassFieldException("The email passed cannot be null or empty.");
		else if (email.length() > 80)
			throw new VoidClassFieldException("The email passed cannot be over 80 chars.");
		else if (!isEmailValid(email))
			throw new VoidClassFieldException("The email passed is not valid.");
		else if (photoPath == null || photoPath.equals(""))
			throw new VoidClassFieldException("The photo path passed cannot be null or empty.");
		else if (photoPath.length() > 80)
			throw new VoidClassFieldException("The photo path passed cannot be over 80 chars.");
		
		try {
			ResultSet rs = DBManager.executeQuery("select count(*) as users_number from users where email = '" + email + "';");
			
			if (rs.next()) {
				if (rs.getInt("users_number") == 0)
					throw new InvalidUserEmailException("Inexistent user email: " + email);
				
				result = DBManager.executeUpdate("update users "
											   + "set photo_path = '" + photoPath + "' "
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
	
	

	// REMOVERS ----------------------------------------------------------------------------------------------

	public synchronized static int removeUser(String email) throws InvalidUserEmailException, GenericSQLException, VoidClassFieldException {
		int result = 0;

		if (email == null || email.equals(""))
			throw new VoidClassFieldException("The email passed cannot be null or empty.");
		else if (email.length() > 80)
			throw new VoidClassFieldException("The email passed cannot be over 80 chars.");
		else if (!isEmailValid(email))
			throw new VoidClassFieldException("The email passed is not valid.");
		
		try {
			ResultSet rs = DBManager.executeQuery("select count(*) as users_number from users where email = '" + email + "';");
			
			if (rs.next()) {	
				if (rs.getInt("users_number") == 0)
					throw new InvalidUserEmailException("Inexistent user email: " + email);
				
				
				
				result = DBManager.executeUpdate("delete from users "
						                       + "where email = '" + email + "';");
			}
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		if (result == 1)
			return 1;
		else
			throw new GenericSQLException("An error occurred while removing user in DB.");
	}

}
