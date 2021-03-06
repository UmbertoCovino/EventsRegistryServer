package server.backend;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import commons.Event;
import commons.User;
import commons.exceptions.GenericSQLException;
import commons.exceptions.InvalidEventIdException;
import commons.exceptions.InvalidParticipationException;
import commons.exceptions.InvalidUserEmailException;
import commons.exceptions.VoidClassFieldException;

public class EventsAccessObject {
	public static final SimpleDateFormat DATETIME_SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	
	
	// GETTERS ----------------------------------------------------------------------------------------------
	
	public synchronized static Integer getNumberOfEvents() throws GenericSQLException {
		Integer eventsNumber = null;
		
		try {
			ResultSet rs = DBManager.executeQuery("select count(*) as events_number from events;");
			
			if (rs.next())
				eventsNumber = rs.getInt("events_number");
			
			rs.close();
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		return eventsNumber;
	}
	
	public synchronized static Event getEvent(int id) throws InvalidEventIdException, GenericSQLException {
		Event event = null;
		
		try {
			ResultSet rs = DBManager.executeQuery("select title, start_date, end_date, description, E.photo_path as e_photo_path, user_owner_email, name, surname, email, U.photo_path as u_photo_path "
											   + "from events E join users U on user_owner_email = email "
											   + "where id = " + id + ";");

			if (rs.next()) {
				String title = rs.getString("title");
				Date startDate = new Date(rs.getTimestamp("start_date").getTime());
				Date endDate = new Date(rs.getTimestamp("end_date").getTime());
				String description = rs.getString("description");
				String photoPath = rs.getString("e_photo_path");
				String ownerEmail = rs.getString("user_owner_email");
				
				String name = rs.getString("name");
				String surname = rs.getString("surname");
				String email = rs.getString("email");
				String userPhotoPath = rs.getString("u_photo_path");
				
				User owner = new User(name, surname, email, userPhotoPath);
				
				event = new Event(id, title, startDate, endDate, description, photoPath, ownerEmail, owner);
			} else
				throw new InvalidEventIdException("Inexistent event id: " + id);
			
			rs.close();
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		return event;
	}
	
	public synchronized static String getEventTitle(int id) throws InvalidEventIdException, GenericSQLException {
		String title = null;
		
		try {
			ResultSet rs = DBManager.executeQuery("select title from events where id = " + id + ";");

			if (rs.next()) {
				title = rs.getString("title");
			} else
				throw new InvalidEventIdException("Inexistent event id: " + id);
			
			rs.close();
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		return title;
	}
	
	public synchronized static Date getEventStartDate(int id) throws InvalidEventIdException, GenericSQLException {
		Date startDate = null;
		
		try {
			ResultSet rs = DBManager.executeQuery("select start_date from events where id = " + id + ";");

			if (rs.next()) {
				startDate = rs.getTimestamp("start_date");
			} else
				throw new InvalidEventIdException("Inexistent event id: " + id);
			
			rs.close();
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		return startDate;
	}
	
	public synchronized static Date getEventEndDate(int id) throws InvalidEventIdException, GenericSQLException {
		Date endDate = null;
		
		try {
			ResultSet rs = DBManager.executeQuery("select end_date from events where id = " + id + ";");

			if (rs.next()) {
				endDate = rs.getTimestamp("end_date");
			} else
				throw new InvalidEventIdException("Inexistent event id: " + id);
			
			rs.close();
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		return endDate;
	}
	
	public synchronized static String getEventDescription(int id) throws InvalidEventIdException, GenericSQLException {
		String description = null;
		
		try {
			ResultSet rs = DBManager.executeQuery("select description from events where id = " + id + ";");

			if (rs.next()) {
				description = rs.getString("description");
			} else
				throw new InvalidEventIdException("Inexistent event id: " + id);
			
			rs.close();
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		return description;
	}
	
	public synchronized static String getEventPhotoPath(int id) throws InvalidEventIdException, GenericSQLException {
		String photoPath = null;
		
		try {
			ResultSet rs = DBManager.executeQuery("select photo_path from events where id = " + id + ";");

			if (rs.next()) {
				photoPath = rs.getString("photo_path");
			} else
				throw new InvalidEventIdException("Inexistent event id: " + id);
			
			rs.close();
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		return photoPath;
	}

	public synchronized static String getEventOwnerEmail(int id) throws InvalidEventIdException, GenericSQLException {
		String ownerEmail = null;
		
		try {
			ResultSet rs = DBManager.executeQuery("select user_owner_email from events where id = " + id + ";");

			if (rs.next()) {
				ownerEmail = rs.getString("user_owner_email");
			} else
				throw new InvalidEventIdException("Inexistent event id: " + id);
			
			rs.close();
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		return ownerEmail;
	}

	public synchronized static User getEventOwner(int id) throws InvalidEventIdException, GenericSQLException {
		User owner = null;
		
		try {
			ResultSet rs = DBManager.executeQuery("select user_owner_email from events where id = " + id + ";");

			if (rs.next()) {
				String ownerEmail = rs.getString("user_owner_email");
				
				try {
					owner = UsersAccessObject.getUser(ownerEmail);
				} catch (InvalidUserEmailException e) {
					owner = null;
				} catch (VoidClassFieldException e) {
					owner = null;
				}
			} else
				throw new InvalidEventIdException("Inexistent event id: " + id);
			
			rs.close();
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		return owner;
	}
	
	public synchronized static ArrayList<User> getEventSubscribers(int id) throws GenericSQLException, InvalidEventIdException {
		ArrayList<User> users = new ArrayList<>();
		
		try {
			ResultSet rs = DBManager.executeQuery("select count(*) as events_number from events where id = " + id + ";");
			
			if (rs.next()) {
				if (rs.getInt("events_number") == 0)
					throw new InvalidEventIdException("Inexistent event id: " + id);
				
				rs = DBManager.executeQuery("select * "
										 + "from users join events_users_participations on email = user_email "
										 + "where event_id = " + id + ";");
				
				while (rs.next()) {
					String email = rs.getString("email");
					String name = rs.getString("name");
					String surname = rs.getString("surname");
					String photoPath = rs.getString("photo_path");
					
					User user = new User(name, surname, email, photoPath);
					
					users.add(user);
				}
				
				rs.close();
			}
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		return users;
	}
	
	public synchronized static ArrayList<Event> getEvents() throws GenericSQLException {
		ArrayList<Event> events = new ArrayList<>();
		
		try {
			ResultSet rs = DBManager.executeQuery("select id, title, start_date, end_date, description, E.photo_path as e_photo_path, user_owner_email, name, surname, email, U.photo_path as u_photo_path "
											   + "from events E join users U on user_owner_email = email "
											   + "order by start_date;");
			
			while (rs.next()) {
				int id = rs.getInt("id");
				String title = rs.getString("title");
				Date startDate = new Date(rs.getTimestamp("start_date").getTime());
				Date endDate = new Date(rs.getTimestamp("end_date").getTime());
				String description = rs.getString("description");
				String photoPath = rs.getString("e_photo_path");
				String ownerEmail = rs.getString("user_owner_email");
				
				String name = rs.getString("name");
				String surname = rs.getString("surname");
				String email = rs.getString("email");
				String userPhotoPath = rs.getString("u_photo_path");
				
				User owner = new User(name, surname, email, userPhotoPath);
				
				Event event = new Event(id, title, startDate, endDate, description, photoPath, ownerEmail, owner);
				
				events.add(event);
			}
			
			rs.close();
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		return events;
	}

	public synchronized static ArrayList<Event> getEventsAfterDate(Date date) throws GenericSQLException, VoidClassFieldException {
		ArrayList<Event> events = new ArrayList<>();
		
		if (date == null)
			throw new VoidClassFieldException("The date passed cannot be null.");
		
		try {
			ResultSet rs = DBManager.executeQuery("select id, title, start_date, end_date, description, E.photo_path as e_photo_path, user_owner_email, name, surname, email, U.photo_path as u_photo_path "
											   + "from events E join users U on user_owner_email = email "
											   + "where start_date >= '" + DATETIME_SDF.format(date) + "' "
											   + "order by start_date;");
			
			while (rs.next()) {
				int id = rs.getInt("id");
				String title = rs.getString("title");
				Date startDate = new Date(rs.getTimestamp("start_date").getTime());
				Date endDate = new Date(rs.getTimestamp("end_date").getTime());
				String description = rs.getString("description");
				String photoPath = rs.getString("e_photo_path");
				String ownerEmail = rs.getString("user_owner_email");
				
				String name = rs.getString("name");
				String surname = rs.getString("surname");
				String email = rs.getString("email");
				String userPhotoPath = rs.getString("u_photo_path");
				
				User owner = new User(name, surname, email, userPhotoPath);
				
				Event event = new Event(id, title, startDate, endDate, description, photoPath, ownerEmail, owner);
				
				events.add(event);
			}
			
			rs.close();
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		return events;
	}

	public synchronized static ArrayList<Event> getEventsBeforeDate(Date date) throws GenericSQLException, VoidClassFieldException {
		ArrayList<Event> events = new ArrayList<>();
		
		if (date == null)
			throw new VoidClassFieldException("The date passed cannot be null.");
		
		try {
			ResultSet rs = DBManager.executeQuery("select id, title, start_date, end_date, description, E.photo_path as e_photo_path, user_owner_email, name, surname, email, U.photo_path as u_photo_path "
											   + "from events E join users U on user_owner_email = email "
											   + "where start_date <= '" + DATETIME_SDF.format(date) + "' "
											   + "order by start_date;");
			
			while (rs.next()) {
				int id = rs.getInt("id");
				String title = rs.getString("title");
				Date startDate = new Date(rs.getTimestamp("start_date").getTime());
				Date endDate = new Date(rs.getTimestamp("end_date").getTime());
				String description = rs.getString("description");
				String photoPath = rs.getString("e_photo_path");
				String ownerEmail = rs.getString("user_owner_email");
				
				String name = rs.getString("name");
				String surname = rs.getString("surname");
				String email = rs.getString("email");
				String userPhotoPath = rs.getString("u_photo_path");
				
				User owner = new User(name, surname, email, userPhotoPath);
				
				Event event = new Event(id, title, startDate, endDate, description, photoPath, ownerEmail, owner);
				
				events.add(event);
			}
			
			rs.close();
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		return events;
	}

	public synchronized static ArrayList<Event> getEventsBetweenTwoDates(Date fromDate, Date toDate) throws GenericSQLException, VoidClassFieldException {
		ArrayList<Event> events = new ArrayList<>();
		
		if (fromDate == null)
			throw new VoidClassFieldException("The first date passed cannot be null.");
		else if (toDate == null)
			throw new VoidClassFieldException("The second date passed cannot be null.");
		
		try {
			ResultSet rs = DBManager.executeQuery("select id, title, start_date, end_date, description, E.photo_path as e_photo_path, user_owner_email, name, surname, email, U.photo_path as u_photo_path "
											   + "from events E join users U on user_owner_email = email "
											   + "where start_date between '" + DATETIME_SDF.format(fromDate) + "' and '" + DATETIME_SDF.format(toDate) + "' "
											   + "order by start_date;");
			
			while (rs.next()) {
				int id = rs.getInt("id");
				String title = rs.getString("title");
				Date startDate = new Date(rs.getTimestamp("start_date").getTime());
				Date endDate = new Date(rs.getTimestamp("end_date").getTime());
				String description = rs.getString("description");
				String photoPath = rs.getString("e_photo_path");
				String ownerEmail = rs.getString("user_owner_email");
				
				String name = rs.getString("name");
				String surname = rs.getString("surname");
				String email = rs.getString("email");
				String userPhotoPath = rs.getString("u_photo_path");
				
				User owner = new User(name, surname, email, userPhotoPath);
				
				Event event = new Event(id, title, startDate, endDate, description, photoPath, ownerEmail, owner);
				
				events.add(event);
			}
			
			rs.close();
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		return events;
	}
	
	// ADDERS -----------------------------------------------------------------------------------------------
	
	public synchronized static int addEvent(Event event) throws InvalidEventIdException, VoidClassFieldException, GenericSQLException {
		int result = 0;
		
		/*if (event.getId() == null)
			throw new VoidClassFieldException("INTERNAL SERVER ERROR. The event passed does not have the id. Set it before calling this method.");
		else */if (event.getOwnerEmail() == null)
			throw new VoidClassFieldException("INTERNAL SERVER ERROR. The event passed does not have the owner email. Set it before calling this method.");
		else if (event.getTitle() == null || event.getTitle().equals("")
			  || event.getStartDate() == null
		      || event.getEndDate() == null
		      || event.getDescription() == null || event.getDescription().equals("")) {
			throw new VoidClassFieldException("The event passed have one or some fields null or empty. Set them before calling this method.");
		}
		
		try {
			ResultSet rs = DBManager.executeQuery("select count(*) as events_number from events where id = " + event.getId() + ";");
				
			if (rs.next()) {
				if (rs.getInt("events_number") > 0)
					throw new InvalidEventIdException("Duplicated event id: " + event.getId());
				
				result = DBManager.executeUpdate("insert into events (title, start_date, end_date, description, photo_path, user_owner_email) "
												   + "values ('" + event.getTitle() + "', "
													       + "'" + event.getFormattedStartDate() + "', "
														   + "'" + event.getFormattedEndDate() + "', "
														   + "'" + event.getDescription() + "', "
														   + ((event.getPhotoPath() != null) ? "'" + event.getPhotoPath() + "', " : "null, ")
														   + "'" + event.getOwnerEmail() + "');");
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
				throw new GenericSQLException("An error occurred while adding event to DB.");
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
	}
	
	public synchronized static int addEventSubscriber(int id, String email) throws InvalidUserEmailException, GenericSQLException, VoidClassFieldException, InvalidEventIdException {
		int result = 0;

		if (email == null || email.equals(""))
			throw new VoidClassFieldException("The email passed cannot be null or empty.");
		
		try {
			ResultSet rs = DBManager.executeQuery("select count(*) as events_number from events where id = " + id + ";");
			
			if (rs.next()) {
				if (rs.getInt("events_number") == 0)
					throw new InvalidEventIdException("Inexistent event id: " + id);
			
				rs = DBManager.executeQuery("select count(*) as users_number from users where email = '" + email + "';");
				
				if (rs.next()) {
					if (rs.getInt("users_number") == 0)
						throw new InvalidUserEmailException("Inexistent user email: " + email);
					
					result = DBManager.executeUpdate("insert into events_users_participations "
												   + "values (" + id + ", "
														   + "'" + email + "');");
				}
			}
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}

		if (result != 1)
			throw new GenericSQLException("An error occurred while adding subscriber to DB.");
		else
			return result;
	}
	
	
	
	// UPDATERS --------------------------------------------------------------------------------------------
	
	public synchronized static int updateEvent(Event event) throws InvalidEventIdException, VoidClassFieldException, GenericSQLException {
		int result = 0;
		
		if (event.getId() == null)
			throw new VoidClassFieldException("INTERNAL SERVER ERROR. The event passed does not have the id. Set it before calling this method.");
		else if (event.getTitle() == null || event.getTitle().equals("")
			  || event.getStartDate() == null
		      || event.getEndDate() == null
		      || event.getDescription() == null || event.getDescription().equals("")) {
			throw new VoidClassFieldException("The event passed have one or some fields null or empty. Set them before calling this method.");
		}
		
		try {
			ResultSet rs = DBManager.executeQuery("select count(*) as events_number from events where id = " + event.getId() + ";");
			
			if (rs.next()) {
				if (rs.getInt("events_number") == 0)
					throw new InvalidEventIdException("Inexistent event id: " + event.getId());
				
				result = DBManager.executeUpdate("update events "
											   + "set title = '" + event.getTitle() + "', "
												   + "start_date = '" + event.getFormattedStartDate() + "', "
												   + "end_date = '" + event.getFormattedEndDate() + "', "
												   + "description = '" + event.getDescription() + "' "
												   + ((event.getPhotoPath() != null) ? ", photo_path = '" + event.getPhotoPath() + "' " : "")
												   + ((event.getOwnerEmail() != null) ? ", user_owner_email = '" + event.getOwnerEmail() + "' " : "")
											   + "where id = '" + event.getId() + "';");
			}
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		if (result == 1)
			return 1;
		else
			throw new GenericSQLException("An error occurred while updating event in DB.");
	}
	
	public synchronized static int updateEventTitle(int id, String title) throws InvalidEventIdException, VoidClassFieldException, GenericSQLException {
		int result = 0;
		
		if (title == null || title.equals(""))
			throw new VoidClassFieldException("The title passed cannot be null or empty.");
		
		try {
			ResultSet rs = DBManager.executeQuery("select count(*) as events_number from events where id = " + id + ";");
			
			if (rs.next()) {
				if (rs.getInt("events_number") == 0)
					throw new InvalidEventIdException("Inexistent event id: " + id);
				
				result = DBManager.executeUpdate("update events "
											   + "set title = '" + title + "' "
											   + "where id = '" + id + "';");
			}
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		if (result == 1)
			return 1;
		else
			throw new GenericSQLException("An error occurred while updating event in DB.");
	}
	
	public synchronized static int updateEventStartDate(int id, Date startDate) throws InvalidEventIdException, VoidClassFieldException, GenericSQLException {
		int result = 0;
		
		if (startDate == null)
			throw new VoidClassFieldException("The start date passed cannot be null.");
		
		try {
			ResultSet rs = DBManager.executeQuery("select count(*) as events_number from events where id = " + id + ";");
			
			if (rs.next()) {
				if (rs.getInt("events_number") == 0)
					throw new InvalidEventIdException("Inexistent event id: " + id);
				
				result = DBManager.executeUpdate("update events "
											   + "set start_date = '" + Event.DATETIME_SDF.format(startDate) + "' "
											   + "where id = '" + id + "';");
			}
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		if (result == 1)
			return 1;
		else
			throw new GenericSQLException("An error occurred while updating event in DB.");
	}
	
	public synchronized static int updateEventEndDate(int id, Date endDate) throws InvalidEventIdException, VoidClassFieldException, GenericSQLException {
		int result = 0;

		if (endDate == null)
			throw new VoidClassFieldException("The end date passed cannot be null.");
		
		try {
			ResultSet rs = DBManager.executeQuery("select count(*) as events_number from events where id = " + id + ";");
			
			if (rs.next()) {
				if (rs.getInt("events_number") == 0)
					throw new InvalidEventIdException("Inexistent event id: " + id);
				
				result = DBManager.executeUpdate("update events "
											   + "set end_date = '" + Event.DATETIME_SDF.format(endDate) + "' "
											   + "where id = '" + id + "';");
			}
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		if (result == 1)
			return 1;
		else
			throw new GenericSQLException("An error occurred while updating event in DB.");
	}
	
	public synchronized static int updateEventDescription(int id, String description) throws InvalidEventIdException, VoidClassFieldException, GenericSQLException {
		int result = 0;

		if (description == null || description.equals(""))
			throw new VoidClassFieldException("The description passed cannot be null or empty.");
		
		try {
			ResultSet rs = DBManager.executeQuery("select count(*) as events_number from events where id = " + id + ";");
			
			if (rs.next()) {
				if (rs.getInt("events_number") == 0)
					throw new InvalidEventIdException("Inexistent event id: " + id);
				
				result = DBManager.executeUpdate("update events "
											   + "set description = '" + description + "' "
											   + "where id = '" + id + "';");
			}
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		if (result == 1)
			return 1;
		else
			throw new GenericSQLException("An error occurred while updating event in DB.");
	}
	
	public synchronized static int updateEventPhotoPath(int id, String photoPath) throws InvalidEventIdException, VoidClassFieldException, GenericSQLException {
		int result = 0;

		if (photoPath == null || photoPath.equals(""))
			throw new VoidClassFieldException("The photo path passed cannot be null or empty.");
		
		try {
			ResultSet rs = DBManager.executeQuery("select count(*) as events_number from events where id = " + id + ";");
			
			if (rs.next()) {
				if (rs.getInt("events_number") == 0)
					throw new InvalidEventIdException("Inexistent event id: " + id);
				
				result = DBManager.executeUpdate("update events "
											   + "set photo_path = '" + photoPath + "' "
											   + "where id = '" + id + "';");
			}
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		if (result == 1)
			return 1;
		else
			throw new GenericSQLException("An error occurred while updating event in DB.");
	}
	
	public synchronized static int updateEventOwnerEmail(int id, String ownerEmail) throws InvalidEventIdException, VoidClassFieldException, GenericSQLException {
		int result = 0;
		
		if (ownerEmail == null || ownerEmail.equals(""))
			throw new VoidClassFieldException("The owner email passed cannot be null or empty.");
		
		try {
			ResultSet rs = DBManager.executeQuery("select count(*) as events_number from events where id = " + id + ";");
			
			if (rs.next()) {
				if (rs.getInt("events_number") == 0)
					throw new InvalidEventIdException("Inexistent event id: " + id);
				
				result = DBManager.executeUpdate("update events "
											   + "set user_owner_email = '" + ownerEmail + "' "
											   + "where id = '" + id + "';");
			}
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		if (result == 1)
			return 1;
		else
			throw new GenericSQLException("An error occurred while updating event in DB.");
	}
	
	

	// REMOVERS ----------------------------------------------------------------------------------------------
	
	public synchronized static int removeEvent(int id) throws InvalidEventIdException, GenericSQLException {
		int result = 0;
		
		try {
			ResultSet rs = DBManager.executeQuery("select count(*) as events_number from events where id = " + id + ";");
			
			if (rs.next()) {			
				if (rs.getInt("events_number") == 0)
					throw new InvalidEventIdException("Inexistent event id: " + id);
				
				
				
				result = DBManager.executeUpdate("delete from events "
						                       + "where id = '" + id + "';");
			}
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		if (result == 1)
			return 1;
		else
			throw new GenericSQLException("An error occurred while removing event in DB.");
	}

	public synchronized static int removeEventSubscriber(int id, String email) throws InvalidEventIdException, GenericSQLException, VoidClassFieldException, InvalidUserEmailException, InvalidParticipationException {
		int result = 0;

		if (email == null || email.equals(""))
			throw new VoidClassFieldException("The email passed cannot be null or empty.");
		
		try {
			ResultSet rs = DBManager.executeQuery("select count(*) as events_number from events where id = " + id + ";");
			
			if (rs.next()) {
				if (rs.getInt("events_number") == 0)
					throw new InvalidEventIdException("Inexistent event id: " + id);
			
				rs = DBManager.executeQuery("select count(*) as users_number from users where email = '" + email + "';");
				
				if (rs.next()) {
					if (rs.getInt("users_number") == 0)
						throw new InvalidUserEmailException("Inexistent user email: " + email);
					
					rs = DBManager.executeQuery("select count(*) as participations_number from events_users_participations where event_id = " + id + " and user_email = '" + email + "';");
					
					if (rs.next()) {
						if (rs.getInt("participations_number") == 0)
							throw new InvalidParticipationException("Inexistent participation id-email: " + id + "-" + email);
						
						result = DBManager.executeUpdate("delete from events_users_participations "
						     						   + "where event_id = " + id + " and user_email = '" + email + "';");
					}
				}
			}
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		if (result == 1)
			return 1;
		else
			throw new GenericSQLException("An error occurred while removing event subscriber in DB.");
	}
}
