package server.backend;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import commons.Event;
import commons.GenericSQLException;
import commons.InvalidEventIdException;
import commons.InvalidUserEmailException;
import commons.User;
import commons.VoidClassFieldException;

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
				}
			} else
				throw new InvalidEventIdException("Inexistent event id: " + id);
			
			rs.close();
		} catch (SQLException e) {
			throw new GenericSQLException(e.getMessage());
		}
		
		return owner;
	}
	
	public synchronized static ArrayList<Event> getEvents() throws GenericSQLException {
		ArrayList<Event> events = new ArrayList<>();
		
		try {
			ResultSet rs = DBManager.executeQuery("select title, start_date, end_date, description, E.photo_path as e_photo_path, user_owner_email, name, surname, email, U.photo_path as u_photo_path "
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

	public static ArrayList<Event> getEventsAfterDate(Date date) throws GenericSQLException {
		ArrayList<Event> events = new ArrayList<>();
		
		try {
			ResultSet rs = DBManager.executeQuery("select title, start_date, end_date, description, E.photo_path as e_photo_path, user_owner_email, name, surname, email, U.photo_path as u_photo_path "
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

	public static ArrayList<Event> getEventsBeforeDate(Date date) throws GenericSQLException {
		ArrayList<Event> events = new ArrayList<>();
		
		try {
			ResultSet rs = DBManager.executeQuery("select title, start_date, end_date, description, E.photo_path as e_photo_path, user_owner_email, name, surname, email, U.photo_path as u_photo_path "
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

	public static ArrayList<Event> getEventsBetweenTwoDates(Date fromDate, Date toDate) throws GenericSQLException {
		ArrayList<Event> events = new ArrayList<>();
		
		try {
			ResultSet rs = DBManager.executeQuery("select title, start_date, end_date, description, E.photo_path as e_photo_path, user_owner_email, name, surname, email, U.photo_path as u_photo_path "
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
		
		if (event.getId() == null && event.getOwnerEmail() == null) {
			if (event.getId() == null)
				throw new VoidClassFieldException("The event passed does not have the id. Set it before calling this method.");
			else
				throw new VoidClassFieldException("The event passed does not have the owner email. Set it before calling this method.");
		} else if (event.getTitle() == null && event.getStartDate() == null
				&& event.getEndDate() == null && event.getDescription() == null
				&& event.getOwnerEmail() == null) {
			throw new VoidClassFieldException("The event passed does not have some field. Set them before calling this method.");
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
														   + ((event.getPhotoPath() != null) ? "'" + event.getPhotoPath() + "', " : "null")
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
	
	
	
	// UPDATERS --------------------------------------------------------------------------------------------
	
	public synchronized static int updateEvent(Event event) throws InvalidEventIdException, VoidClassFieldException, GenericSQLException {
		int result = 0;
		
		if (event.getId() == null) {
			throw new VoidClassFieldException("The event passed does not have the id. Set it before calling this method.");
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
}
