package server.web.resources.json;

import com.google.gson.Gson;
import com.sun.scenario.Settings;
import commons.User;
import commons.exceptions.GenericSQLException;
import commons.exceptions.InvalidUserEmailException;
import commons.exceptions.VoidClassFieldException;
import server.backend.DBManager;
import server.backend.UsersAccessObject;
import server.web.frontend.EventsRegistryWebApplication;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class LaunchServerApp {
	private static boolean alreadyRunned = false;
	
	public synchronized static void execute() throws InvalidUserEmailException, VoidClassFieldException, GenericSQLException, IOException {
		if (!alreadyRunned) {
			// aggiungo un utente per testare l'inserimento nel realm del server di utenti presenti nel db
			Gson gson = EventsRegistryWebApplication.GSON;
			BufferedReader br = new BufferedReader(new FileReader("settings.json"));
			Settings settings = gson.fromJson(br, Settings.class);
			br.close();
			DBManager.DB_NAME = settings.dbName;
			DBManager.DB_USER = settings.dbUser;
			DBManager.DB_PASSWORD = settings.dbPassword;
			/* codice copiato dal main di EventsRegistryWebApplication - sarebbe opportuno isolare la fase
			di configurazione delle opzioni del database per renderle riusabile*/

			User user = new User("name_test", "surname_test",
					"email_test_LOOP_REALM@gmail.com", "password_test",
					"email_test_LOOP_REALM.jpg");
			UsersAccessObject.addUser(user);

			EventsRegistryWebApplication.main(null);
			alreadyRunned = true;
		}
	}

	private class Settings {
		public String dbName;
		public String dbUser;
		public String dbPassword;
	}
}
