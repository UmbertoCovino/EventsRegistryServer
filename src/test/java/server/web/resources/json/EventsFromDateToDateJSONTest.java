package server.web.resources.json;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import commons.Event;
import commons.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;

import server.backend.DBManager;
import server.backend.EventsAccessObject;
import server.web.frontend.EventsRegistryWebApplication;

class EventsFromDateToDateJSONTest {

	private String url = "http://localhost:8182/eventsRegistry/events";
	private static Gson gson;
	private static Client client = new Client(Protocol.HTTP);
	private static int eventId;

	@BeforeAll
	public static void setUpBeforeAll() throws Exception {
		LaunchServerApp.execute();
		gson = EventsRegistryWebApplication.GSON;

		// add a user for resource with guard
		String url_users = "http://localhost:8182/eventsRegistry/users";
		Request request = new Request(Method.POST, url_users);
		User user = new User("name_test", "surname_test", "email_test@gmail.com", "password_test", "email_test.jpg");
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		client.handle(request);

		//adding an event to subscribe to it
		Event event = new Event("title_test", Event.DATETIME_SDF.parse("2020-01-11 9:26:00"),
				Event.DATETIME_SDF.parse("2020-01-11 10:26:00"), "description_test");
		event.setOwnerEmail(user.getEmail());
		eventId = EventsAccessObject.addEvent(event);
	}
	
	@AfterAll
	public static void tearDownAfterClass() throws Exception {
		DBManager.executeUpdate("delete from events;");
	}
	
	@BeforeEach
	public void setUp() throws Exception {
	}
	
	@AfterEach
	public void tearDown() throws Exception {
	}
	
	@Test
	/* all parameter are valid ---> 200 OK */
	public void testGet1() {
		// dovrei aggiungere un evento dopo e verificare che il GET abbia restituito quello
		String from = "2020-01-10 10:00:00";
		String to = "2020-01-12 10:00:00";
		Request request = new Request(Method.GET, url + "/between/" + from + "/" + to);
		Response jsonResponse = client.handle(request);

		assertEquals(200, jsonResponse.getStatus().getCode());
	}
	
	@Test
	/* invalid format for date attribute ---> DATE_PARSING = 953 */
	public void testGet2() {
		// dovrei aggiungere un evento dopo e verificare che il GET abbia restituito quello
		String from = "2020.01.10 10:00:00";
		String to = "2020.01.12 10:00:00";
		Request request = new Request(Method.GET, url + "/between/" + from + "/" + to);
		Response jsonResponse = client.handle(request);;

		assertEquals(953, jsonResponse.getStatus().getCode());
	}
	
	/* ci sono nel programma controlli per verificare la compatibilità logica tra le due date? 
	 * Esempio se metto come from una data successiva a to, che succede? 
	 * ci deve essere? */

}
