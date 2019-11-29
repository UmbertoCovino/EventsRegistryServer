package server.web.frontend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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

import commons.InvalidUserEmailException;
import server.web.resources.json.EventsRegistrySizeJSON;
import server.backend.TelegramBot;
import server.backend.wrapper.EventsRegistryAPI;
import server.backend.wrapper.UsersRegistryAPI;
import server.web.resources.json.EventJSON;
import server.web.resources.json.EventPhotoJSON;
import server.web.resources.json.EventStartTimeJSON;
import server.web.resources.json.EventDateJSON;
import server.web.resources.json.EventDescriptionJSON;
import server.web.resources.json.EventEndTimeJSON;
import server.web.resources.json.EventTitleJSON;
import server.web.resources.json.EventUserJSON;
import server.web.resources.json.EventsRegistryEventsAfterDateJSON;
import server.web.resources.json.EventsRegistryEventsBeforeDateJSON;
import server.web.resources.json.EventsRegistryJSON;
import server.web.resources.json.EventsRegistryEventsFromDateToDateJSON;
import server.web.resources.json.UserEventsJSON;
import server.web.resources.json.UserJSON;
import server.web.resources.json.UserNameJSON;
import server.web.resources.json.UserPasswordJSON;
import server.web.resources.json.UserPhotoJSON;
import server.web.resources.json.UserSurnameJSON;
import server.web.resources.json.UsersRegistryJSON;
import server.web.resources.json.UsersRegistrySizeJSON;

public class EventsRegistryWebApplication extends Application {
	private static String rootDirForWebStaticFiles;
	private MapVerifier verifier; 
	
	private class Settings {
		public int port;
		public String web_dir;
		public String storage_dir;
		public String events_storage_file;
		public String users_storage_file;
		public String events_photos_dir;
		public String users_photos_dir;
	}
		
    /**
     * Creates a root Restlet that will receive all incoming calls.
     */
    @Override
    public Restlet createInboundRoot() {
		// Create a router Restlet that routes each call to a new instance appropriate ServerResource.
		Router router = new Router(getContext());
		
		Directory webStaticFilesDirectory = new Directory(getContext(), rootDirForWebStaticFiles);
		webStaticFilesDirectory.setListingAllowed(true);
		webStaticFilesDirectory.setDeeplyAccessible(true);
		
		verifier = new MapVerifier();
		
		UsersRegistryAPI urapi = UsersRegistryAPI.instance();
		for (String email: urapi.emails())
			try {
				verifier.getLocalSecrets().put(email, urapi.get(email).getPassword().toCharArray());
			} catch (InvalidUserEmailException e) {
				e.printStackTrace();
			}
			
		getContext().setDefaultVerifier(verifier);
		
		router.attach("/eventsRegistry/web", webStaticFilesDirectory);
		router.attach("/eventsRegistry/web/", webStaticFilesDirectory);
		
		router.attach("/eventsRegistry/events", getGuardExcludingGet(EventsRegistryJSON.class));
		router.attach("/eventsRegistry/events/size", EventsRegistrySizeJSON.class);									// not used
		router.attach("/eventsRegistry/events/after/{date}", EventsRegistryEventsAfterDateJSON.class);
		router.attach("/eventsRegistry/events/before/{date}", EventsRegistryEventsBeforeDateJSON.class);
		router.attach("/eventsRegistry/events/between/{from}/{to}", EventsRegistryEventsFromDateToDateJSON.class);
		router.attach("/eventsRegistry/events/{id}", getGuardExcludingGet(EventJSON.class));						// not used
		router.attach("/eventsRegistry/events/{id}/title", getGuardExcludingGet(EventTitleJSON.class));				// not used
		router.attach("/eventsRegistry/events/{id}/date", getGuardExcludingGet(EventDateJSON.class));				// not used
		router.attach("/eventsRegistry/events/{id}/startTime", getGuardExcludingGet(EventStartTimeJSON.class));		// not used
		router.attach("/eventsRegistry/events/{id}/endTime", getGuardExcludingGet(EventEndTimeJSON.class));			// not used
		router.attach("/eventsRegistry/events/{id}/description", getGuardExcludingGet(EventDescriptionJSON.class));	// not used
		router.attach("/eventsRegistry/events/{id}/photo", getGuardExcludingGet(EventPhotoJSON.class));
		router.attach("/eventsRegistry/events/{id}/user", getGuardExcludingGet(EventUserJSON.class));				// not used
		
		router.attach("/eventsRegistry/users", getGuardExcludingGetAndPost(UsersRegistryJSON.class));
		router.attach("/eventsRegistry/users/size", UsersRegistrySizeJSON.class);									// not used
		router.attach("/eventsRegistry/users/{email}", getGuardExcludingGetAndPost(UserJSON.class));
		router.attach("/eventsRegistry/users/{email}/name", getGuardExcludingGet(UserNameJSON.class));				// not used
		router.attach("/eventsRegistry/users/{email}/surname", getGuardExcludingGet(UserSurnameJSON.class));		// not used
		router.attach("/eventsRegistry/users/{email}/password", getGuard(UserPasswordJSON.class));					// not used
		router.attach("/eventsRegistry/users/{email}/photo", getGuardExcludingGet(UserPhotoJSON.class));
		router.attach("/eventsRegistry/users/{email}/events", getGuard(UserEventsJSON.class));						// not used
		
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
			
			System.err.println("Settings were loaded from file.");
		} catch (Exception e) {
			System.err.println("Settings file not found!");
			System.exit(-1);
		}
		
		rootDirForWebStaticFiles = "file:" + File.separator + File.separator + System.getProperty("user.dir") + File.separator + settings.web_dir;
		System.err.println("Web directory: " + rootDirForWebStaticFiles);
		
		String storageDirectory = System.getProperty("user.dir") + File.separator + settings.storage_dir + File.separator;
		String eventsPhotosDirectory  = storageDirectory + settings.events_photos_dir + File.separator;
		String usersPhotosDirectory  = storageDirectory + settings.users_photos_dir + File.separator;
		
		createDirectoryIfNotExists(storageDirectory);
		createDirectoryIfNotExists(eventsPhotosDirectory);
		createDirectoryIfNotExists(usersPhotosDirectory);
		
		EventsRegistryAPI erapi = EventsRegistryAPI.instance();
		erapi.setStorageFiles(storageDirectory, settings.events_storage_file, eventsPhotosDirectory);
		erapi.restore();
		
		UsersRegistryAPI urapi = UsersRegistryAPI.instance();
		urapi.setStorageFiles(storageDirectory, settings.users_storage_file, usersPhotosDirectory);
		urapi.restore();
		
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
        
        System.err.println();
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
}
   