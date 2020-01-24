package server.web.resources.json;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;

import com.google.gson.Gson;

import commons.Event;
import commons.User;
import commons.exceptions.GenericSQLException;
import commons.exceptions.InvalidEventIdException;
import commons.exceptions.InvalidUserEmailException;
import commons.exceptions.VoidClassFieldException;
import server.backend.DBManager;
import server.backend.EventsAccessObject;
import server.backend.UsersAccessObject;
import server.web.frontend.EventsRegistryWebApplication;

class EventSubscribersJSONTest {

	private static Gson gson;
	private static String url = "http://localhost:8182/eventsRegistry/events";
	private static Client client;
	private static int eventId;
	
	@BeforeAll
	public static void setUpBeforeAll() throws Exception {				
		LaunchServerApp.execute();
		gson = EventsRegistryWebApplication.GSON;
		client = new Client(Protocol.HTTP);

		DBManager.executeUpdate("delete from events_users_participations;");
		DBManager.executeUpdate("delete from events;");
		DBManager.executeUpdate("delete from users;");
		
		// add a user for resource with guard
		User user = new User("name_test", "surname_test", "email_test", "password_test", "email_test.jpg");
		UsersAccessObject.addUser(user);
			
		//adding an event to subscribe to it
		Event event = new Event("title_test", Event.DATETIME_SDF.parse("2020-01-18 19:26:00"), 
				Event.DATETIME_SDF.parse("2020-01-24 10:26:00"), "description_test");
		event.setOwnerEmail(user.getEmail());
		eventId = EventsAccessObject.addEvent(event);
	}
	
	@AfterAll
	public static void tearDownAfterClass() throws Exception {
		DBManager.executeUpdate("delete from events_users_participations;");
		DBManager.executeUpdate("delete from events;");
		DBManager.executeUpdate("delete from users;");
	}
	
	@BeforeEach
	public void setUp() throws Exception {
		DBManager.executeUpdate("delete from events_users_participations;");
	}
	
	@AfterEach
	public void tearDown() throws Exception {
	}
	
	///////////////////////////////////////////////GET////////////////////////////////////////////
	
	@Test
	public void testGet1() {
		Request request = new Request(Method.GET, url + "/" + eventId + "/subscribers");
		Response jsonResponse = client.handle(request);

		assertEquals(200, jsonResponse.getStatus().getCode());
	}
	
	//////////////////////////////////////////////POST/////////////////////////////////////////////
	
	@Test
	/* utente not subscribered yet ---> 200 OK */
	public void testPost1() {
		Request request = new Request(Method.POST, url + "/" + eventId + "/subscribers");
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, 
				"email_test", "password_test");
		String email = "email_test";
		request.setChallengeResponse(challengeResponse);	
		request.setEntity(gson.toJson(email, String.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);
		
		assertEquals(200, jsonResponse.getStatus().getCode());
	}
	
	@Test
	/* utente subscribered yet ---> GENERIC_SQL = 951 */
	public void testPost() throws InvalidUserEmailException, GenericSQLException, VoidClassFieldException, InvalidEventIdException {
		String email = "email_test";
		
		// precondition
		EventsAccessObject.addEventSubscriber(eventId, email);		
		
		Request request = new Request(Method.POST, url + "/" + eventId + "/subscribers");
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, 
				"email_test", "password_test");
		request.setChallengeResponse(challengeResponse);	
		request.setEntity(gson.toJson(email, String.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);
		
		assertEquals(951, jsonResponse.getStatus().getCode());
	}
	
	/////////////////////////////////////////////DELETE//////////////////////////////////////////////////////
	
	@Test
	/* utente not subscribered yet ---> INVALID_PARTICIPATION = 956 */
	public void testDelete1() {
		Request request = new Request(Method.DELETE, url + "/" + eventId + "/subscribers");
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, 
				"email_test", "password_test");
		String email = "email_test";
		request.setChallengeResponse(challengeResponse);	
		request.setEntity(gson.toJson(email, String.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);
		
		assertEquals(956, jsonResponse.getStatus().getCode());
	}
	
	@Test
	/* utente subscribered yet ---> 200 OK */
	public void testDelete2() throws InvalidUserEmailException, GenericSQLException, VoidClassFieldException, InvalidEventIdException {
		String email = "email_test";
		
		// precondition
		EventsAccessObject.addEventSubscriber(eventId, email);		
		
		Request request = new Request(Method.DELETE, url + "/" + eventId + "/subscribers");
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, 
				"email_test", "password_test");
		request.setChallengeResponse(challengeResponse);	
		request.setEntity(gson.toJson(email, String.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);
		
		assertEquals(200, jsonResponse.getStatus().getCode());
	}
	
	
	
	
	
	

}
