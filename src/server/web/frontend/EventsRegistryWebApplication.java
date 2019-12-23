package server.web.frontend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.resource.Directory;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.security.MapVerifier;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import commons.Event;
import commons.User;
import commons.exceptions.GenericSQLException;
import commons.exceptions.InvalidUserEmailException;
import commons.exceptions.VoidClassFieldException;
import server.web.resources.json.EventsSizeJSON;
import server.backend.DBManager;
import server.backend.EventsAccessObject;
import server.backend.TelegramBot;
import server.backend.UsersAccessObject;
import server.web.resources.json.EventJSON;
import server.web.resources.json.EventPhotoJSON;
import server.web.resources.json.EventStartDateJSON;
import server.web.resources.json.EventDescriptionJSON;
import server.web.resources.json.EventEndDateJSON;
import server.web.resources.json.EventTitleJSON;
import server.web.resources.json.EventUserOwnerJSON;
import server.web.resources.json.EventsAfterDateJSON;
import server.web.resources.json.EventsBeforeDateJSON;
import server.web.resources.json.EventsJSON;
import server.web.resources.json.EventsFromDateToDateJSON;
import server.web.resources.json.UserEventsJSON;
import server.web.resources.json.UserJSON;
import server.web.resources.json.UserNameJSON;
import server.web.resources.json.UserPasswordJSON;
import server.web.resources.json.UserPhotoJSON;
import server.web.resources.json.UserSurnameJSON;
import server.web.resources.json.UsersJSON;
import server.web.resources.json.UsersSizeJSON;

public class EventsRegistryWebApplication extends Application {
	public static String STORAGE_DIRECTORY,
						EVENTS_PHOTOS_DIRECTORY,
						USERS_PHOTOS_DIRECTORY;
	
	public static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(Date.class, new DateTypeAdapter())
			.create();

	private static String ROOT_DIR_FOR_WEB_STATIC_FILES;
	
	private MapVerifier verifier; 
	
	private class Settings {
		public int port;
		public String webDir;
		public String storageDir;
		public String eventsPhotosDir;
		public String usersPhotosDir;
		public String dbName;
		public String dbUser;
		public String dbPassword;
		
		public boolean hasSomeVoidField() {
			return !(port != 0
					 && webDir != null
					 && storageDir != null
					 && eventsPhotosDir != null
					 && usersPhotosDir != null
					 && dbName != null
					 && dbUser != null
					 && dbPassword != null
					 && !webDir.equals("")
					 && !storageDir.equals("")
					 && !eventsPhotosDir.equals("")
					 && !usersPhotosDir.equals("")
					 && !dbName.equals("")
					 && !dbUser.equals("")
					 && !dbPassword.equals(""));
		}
	}
		
    /**
     * Creates a root Restlet that will receive all incoming calls.
     */
    @Override
    public Restlet createInboundRoot() {
		// Create a router Restlet that routes each call to a new instance appropriate ServerResource.
		Router router = new Router(getContext());
		
		Directory webStaticFilesDirectory = new Directory(getContext(), ROOT_DIR_FOR_WEB_STATIC_FILES);
		webStaticFilesDirectory.setListingAllowed(true);
		webStaticFilesDirectory.setDeeplyAccessible(true);
		
		verifier = new MapVerifier();
		
		try {
			for (String email : UsersAccessObject.getUsersEmails())
				verifier.getLocalSecrets().put(email, UsersAccessObject.getUserPassword(email).toCharArray());
		} catch (InvalidUserEmailException e) {
			e.printStackTrace();
		} catch (GenericSQLException e) {
			e.printStackTrace();
		} catch (VoidClassFieldException e) {
			e.printStackTrace();
		}
			
		getContext().setDefaultVerifier(verifier);
		
		router.attach("/eventsRegistry/web", webStaticFilesDirectory);
		router.attach("/eventsRegistry/web/", webStaticFilesDirectory);
		
		router.attach("/eventsRegistry/events", getGuardExcludingGet(EventsJSON.class));
		router.attach("/eventsRegistry/events/size", EventsSizeJSON.class);									// not used
		router.attach("/eventsRegistry/events/after/{date}", EventsAfterDateJSON.class);
		router.attach("/eventsRegistry/events/before/{date}", EventsBeforeDateJSON.class);
		router.attach("/eventsRegistry/events/between/{from}/{to}", EventsFromDateToDateJSON.class);
		router.attach("/eventsRegistry/events/{id}", getGuardExcludingGet(EventJSON.class));						// not used
		router.attach("/eventsRegistry/events/{id}/title", getGuardExcludingGet(EventTitleJSON.class));				// not used
		router.attach("/eventsRegistry/events/{id}/startDate", getGuardExcludingGet(EventStartDateJSON.class));		// not used
		router.attach("/eventsRegistry/events/{id}/endDate", getGuardExcludingGet(EventEndDateJSON.class));			// not used
		router.attach("/eventsRegistry/events/{id}/description", getGuardExcludingGet(EventDescriptionJSON.class));	// not used
		router.attach("/eventsRegistry/events/{id}/photo", getGuardExcludingGet(EventPhotoJSON.class));
		router.attach("/eventsRegistry/events/{id}/userOwner", getGuardExcludingGet(EventUserOwnerJSON.class));				// not used				// not used
		router.attach("/eventsRegistry/events/{id}/subscribers", getGuard(EventSubscribersJSON.class));	
		
		router.attach("/eventsRegistry/users", getGuardExcludingGetAndPost(UsersJSON.class));
		router.attach("/eventsRegistry/users/size", UsersSizeJSON.class);									// not used
		router.attach("/eventsRegistry/users/{email}", getGuardExcludingGetAndPost(UserJSON.class));
		router.attach("/eventsRegistry/users/{email}/name", getGuardExcludingGet(UserNameJSON.class));				// not used
		router.attach("/eventsRegistry/users/{email}/surname", getGuardExcludingGet(UserSurnameJSON.class));		// not used
		router.attach("/eventsRegistry/users/{email}/password", getGuard(UserPasswordJSON.class));					// not used
		router.attach("/eventsRegistry/users/{email}/photo", getGuardExcludingGet(UserPhotoJSON.class));
		router.attach("/eventsRegistry/users/{email}/events", getGuard(UserEventsJSON.class));						// not used
		router.attach("/eventsRegistry/users/{email}/telegram", UserTelegramJSON.class);
		
//		try {
//			urapi.remove("ciao@er.it");
//		} catch (InvalidUserEmailException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
				
//		EventsRegistryAPI erapi = EventsRegistryAPI.instance();
//		try {
//			for (String id: erapi.ids()) {
//				Event event = erapi.get(id);
//			}
//		} catch (InvalidEventIdException e) {
//			e.printStackTrace();
//		}
//		erapi.commit();
		
		return router;
    }

    private ChallengeAuthenticator getGuard(Class<? extends ServerResource> resourceClass) {
    	ChallengeAuthenticator guard = new ChallengeAuthenticator(getContext(), ChallengeScheme.HTTP_BASIC, "SampleRealm");
		guard.setVerifier(verifier);
		guard.setNext(resourceClass);
        
		return guard;
    }
    
    private ChallengeAuthenticator getGuardExcludingGet(Class<? extends ServerResource> resourceClass) {
		ChallengeAuthenticator guard = new ChallengeAuthenticator(getContext(), ChallengeScheme.HTTP_BASIC, "SampleRealm") {
			@Override
			protected int beforeHandle(Request request, Response response) {
				if (request.getMethod() == Method.GET)
					return CONTINUE;
				else
					return super.beforeHandle(request, response);
			}
		};
		guard.setVerifier(verifier);
		guard.setNext(resourceClass);
    
		return guard;
    }
    
    private ChallengeAuthenticator getGuardExcludingGetAndPost(Class<? extends ServerResource> resourceClass) {
		ChallengeAuthenticator guard = new ChallengeAuthenticator(getContext(), ChallengeScheme.HTTP_BASIC, "SampleRealm") {
			@Override
			protected int beforeHandle(Request request, Response response) {
				if (request.getMethod() == Method.GET || request.getMethod() == Method.POST)
					return CONTINUE;
				else
					return super.beforeHandle(request, response);
			}
		};
		guard.setVerifier(verifier);
		guard.setNext(resourceClass);
    
		return guard;
    }
        
	public static void main(String[] args) {
		Gson gson = new Gson();
		Settings settings = null;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader("settings.json"));
			settings = gson.fromJson(br, Settings.class);
			br.close();
			
			if (settings.hasSomeVoidField()) {
				System.err.println("Settings have some void field! Please, fill all fields.");
				System.exit(-1);
			} else
				System.err.println("Settings were loaded from file.\n");
		} catch (Exception e) {
			System.err.println("Settings file not found!");
			System.exit(-1);
		}
		
		ROOT_DIR_FOR_WEB_STATIC_FILES = "file:" + File.separator + File.separator + System.getProperty("user.dir") + File.separator + settings.webDir;
		createDirectoryIfNotExists(System.getProperty("user.dir") + File.separator + settings.webDir);
		System.err.println("Web directory: " + ROOT_DIR_FOR_WEB_STATIC_FILES + "\n");
		
		STORAGE_DIRECTORY = System.getProperty("user.dir") + File.separator + settings.storageDir + File.separator;
		EVENTS_PHOTOS_DIRECTORY  = STORAGE_DIRECTORY + settings.eventsPhotosDir + File.separator;
		USERS_PHOTOS_DIRECTORY  = STORAGE_DIRECTORY + settings.usersPhotosDir + File.separator;
		
		createDirectoryIfNotExists(STORAGE_DIRECTORY);
		createDirectoryIfNotExists(EVENTS_PHOTOS_DIRECTORY);
		createDirectoryIfNotExists(USERS_PHOTOS_DIRECTORY);
		
		DBManager.DB_NAME = settings.dbName;
		DBManager.DB_USER = settings.dbUser;
		DBManager.DB_PASSWORD = settings.dbPassword;
		
		if (!DBManager.isConnectionEstablished()) {
			System.err.println("MySQL server not available! Ensure that it is running.");
			System.exit(-1);
		}
		
		try {
			// Create a new Component
			Component component = new Component();
			
			// Add a new HTTP server listening on port defined in the settings file
			component.getServers().add(Protocol.HTTP, settings.port);
			
			// Add an handler for static files
			component.getClients().add(Protocol.FILE);
			
			// Attach the EventsRegistryWebApplication application
		    component.getDefaultHost().attach(new EventsRegistryWebApplication());
		      	        
		    // Start the component
		    component.start();
		} catch (Exception e) {	// Something is wrong
			e.printStackTrace();
		}
		
		
		// Launch TelegramBot --------------------------
		// Initialize Api Context
        ApiContextInitializer.init();

        // Instantiate Telegram Bots API
        TelegramBotsApi botsApi = new TelegramBotsApi();

        // Register our bot
        try {
            botsApi.registerBot(new TelegramBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
		// End TelegramBot ------------------------------
        
		
        System.err.println();
        
        
        // testing method
        //eventualTestingPart();
	}

	private static void eventualTestingPart() {
		try {
//			EventsAccessObject.addEvent(new Event("provaT", new Date(), new Date(), "provaD", "path", "email"));
//			EventsAccessObject.addEvent(new Event("provaT2", new Date(), new Date(), "provaD2", "path", "email"));
//
//			EventsAccessObject.updateEvent(new Event(1, "provaTMod", new Date(), new Date(), "provaDMod"));

//			EventsAccessObject.removeEvent(5);
//			
//			System.out.println(EventsAccessObject.getEvent(1));
//	
//			System.out.println(EventsAccessObject.getEvents());
//			System.out.println(EventsAccessObject.getNumberOfEvents());
			
			
			
			
			// da testare
//			UsersAccessObject.addUser(new User("name4", "surname4", "email5", "pass5", "path4"));
//			UsersAccessObject.addUser(new User("name2", "surname2", "email2", "pass2", "path"));

			// da testare 
//			UsersAccessObject.updateUser(new User("nameMod", "surnameMod", "email", "WW", "pathMod"));

			// da testare
//			UsersAccessObject.removeUser("email");
			
//			System.out.println(UsersAccessObject.getUser("email2"));
	
//			System.out.println(UsersAccessObject.getUsers());
//			System.out.println(UsersAccessObject.getNumberOfUsers());
			
			
			
//			System.exit(-1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static boolean createDirectoryIfNotExists(String directoryPath) {
		File directory = new File(directoryPath);

		if (!directory.exists()) {
		    try {
		        directory.mkdir();
		        return true;
		    } catch (SecurityException e){
		        return false;
		    }
		}
		
		return true;
	}
	
	static class DateTypeAdapter extends TypeAdapter<Date> {

		@Override
		public void write(JsonWriter out, Date value) throws IOException {
			if (value != null)
				out.value(Event.DATETIME_SDF.format(value));
			else
				out.nullValue();
		}

		@Override
		public Date read(JsonReader in) throws IOException {
			try {
				return Event.DATETIME_SDF.parse(in.nextString());
			} catch (ParseException e) {
				throw new IOException("Invalid format for datetime: correct one is 'yyyy-MM-dd HH:mm:ss'.");
			}
		}
	}

}
   