package server.backend;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import commons.GenericSQLException;
import commons.InvalidUserEmailException;
import commons.User;

public class UsersAccessObject {
	
	
	
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
	
	public synchronized static User getUser(String email) throws InvalidUserEmailException, GenericSQLException {
		User user = null;
		
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
	
	public synchronized static String getUserName(String email) throws InvalidUserEmailException, GenericSQLException {
		String name = null;
		
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
	
	public synchronized static String getUserSurname(String email) throws InvalidUserEmailException, GenericSQLException {
		String surname = null;
		
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
	
	public synchronized static String getUserPassword(String email) throws InvalidUserEmailException, GenericSQLException {
		String password = null;
		
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
	
	public synchronized static String getUserPhotoPath(String email) throws InvalidUserEmailException, GenericSQLException {
		String photoPath = null;
		
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

//	public static String[] getUsersEmails() {
//		return null;
//	}

	
	
	// ADDERS -----------------------------------------------------------------------------------------------

	public synchronized static int addUser(User user) throws InvalidUserEmailException, GenericSQLException {
		int result = 0;
		
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
													   + "'" + user.getPhotoPath() + "');");
			}
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}

		try {
			if (result == 1) {
				ResultSet rs = DBManager.executeQuery("select last_insert_id() as last_inserted_id;");
				
				if (rs.next()) {
					return rs.getInt("last_inserted_id");
				} else
					return -1;
			} else
				throw new GenericSQLException("An error occurred while adding user to DB.");
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
	}
	
	
	
	// UPDATERS --------------------------------------------------------------------------------------------

	public synchronized static int updateUser(User user) throws InvalidUserEmailException, GenericSQLException {
		int result = 0;
		
		try {
			ResultSet rs = DBManager.executeQuery("select count(*) as users_number from users where email = '" + user.getEmail() + "';");
			
			if (rs.next()) {
				if (rs.getInt("users_number") == 0)
					throw new InvalidUserEmailException("Inexistent user email: " + user.getEmail());
				
				result = DBManager.executeUpdate("update users "
											   + "set name = '" + user.getName() + "', "
												   + "surname = '" + user.getSurname() + "', "
												   + "email = '" + user.getEmail() + "', "
												   + ((user.getPassword() != null) ? "password = '" + user.getPassword() + "', " : "")
												   + "photo_path = '" + user.getPhotoPath() + "' "
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

	public synchronized static int updateUserName(String email, String name) throws InvalidUserEmailException, GenericSQLException {
		int result = 0;
		
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

	public synchronized static int updateUserSurname(String email, String surname) throws InvalidUserEmailException, GenericSQLException {
		int result = 0;
		
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

	public synchronized static int updateUserPassword(String email, String password) throws InvalidUserEmailException, GenericSQLException {
		int result = 0;
		
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

	public synchronized static int updateUserPhotoPath(String email, String photoPath) throws InvalidUserEmailException, GenericSQLException {
		int result = 0;
		
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

	public synchronized static int removeUser(String email) throws InvalidUserEmailException, GenericSQLException {
		int result = 0;
		
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
