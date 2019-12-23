package server.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBManager {
	public static String DB_NAME;
	public static String DB_USER;
	public static String DB_PASSWORD;
	private static final String REFERENCED_LIBRARY = "com.mysql.cj.jdbc.Driver";
	
	private static DBManager instance;
	private Connection connection;
	private Statement statement;
	private String url;

	private DBManager() {
		try {
			Class.forName(REFERENCED_LIBRARY);
		
			url = "jdbc:mysql://localhost:3306/" + DB_NAME + "?autoReconnect=true&createDatabaseIfNotExist=true&useUnicode=true&serverTimezone=Europe/Amsterdam&useSSL=false&allowPublicKeyRetrieval=true";
			
			connection = DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
			statement = connection.createStatement();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isConnectionEstablished() {
		return !(DBManager.getInstance().connection == null);
	}

	private static synchronized DBManager getInstance() {
		if (instance == null)
			instance = new DBManager();
		else {
			try {
				if (instance.connection.isClosed())
					instance.connection = DriverManager.getConnection(instance.url, DB_USER, DB_PASSWORD);
				
				if (instance.statement.isClosed())
					instance.statement = instance.connection.createStatement();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return instance;
	}
	
	
	
	// -----------------------------------------------------------------------------
	
	
	
	public static synchronized ResultSet executeQuery(String query) throws SQLException {
//		System.err.println(query); // debug
		return DBManager.getInstance().statement.executeQuery(query);
	}
	
	public static synchronized int executeUpdate(String query) throws SQLException {
//		System.err.println(query); // debug
		return DBManager.getInstance().statement.executeUpdate(query);
	}
}
