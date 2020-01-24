package server.web.resources.json;

import server.web.frontend.EventsRegistryWebApplication;

public class LaunchServerApp {
	
	private static boolean alreadyRunned = false;
	
	public synchronized static void execute() {
		if(!alreadyRunned) {
			EventsRegistryWebApplication.main(null);
		}
	}

}
