package server.web.resources.json;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import commons.Event;
import commons.User;
import server.backend.DBManager;
import server.web.frontend.EventsRegistryWebApplication;

class EventsJSONTest {

	private static Gson gson;
	private String url = "http://localhost:8182/eventsRegistry/events";
	private Client client = new Client(Protocol.HTTP);
	
	@BeforeAll
	public static void setUpBeforeAll() throws Exception {				
		LaunchServerApp.execute();
		gson = EventsRegistryWebApplication.GSON;

		DBManager.executeUpdate("delete from users;");
		
		// add a user for resource with guard		
		String url = "http://localhost:8182/eventsRegistry/users";
		Client client = new Client(Protocol.HTTP);
		Request request = new Request(Method.POST, url);
		User user = new User("name_test", "surname_test", "email_test", "password_test", null);
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		client.handle(request);
	}
	
	@AfterAll
	public static void tearDownAfterClass() throws Exception {
		DBManager.executeUpdate("delete from users;");
	}
	
	@BeforeEach
	public void setUp() throws Exception {
		DBManager.executeUpdate("delete from events;");
	}
	
	@AfterEach
	public void tearDown() throws Exception {
		DBManager.executeUpdate("delete from events;");
	}
	
	/////////////////////////////////////////////GET///////////////////////////////////////////////
	
	@Test
	public void testGet1() {
		Request request = new Request(Method.GET, url);
		Response jsonResponse = client.handle(request);

		assertEquals(200, jsonResponse.getStatus().getCode());
	}
	
	/////////////////////////////////////////////POST///////////////////////////////////////////////
	
	@Test
	/* all parameter are valid 200 OK */
	public void testPost1() throws ParseException {
		Request request = new Request(Method.POST, url);
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, 
				"email_test", "password_test");
		Event event = new Event("title_test", Event.DATETIME_SDF.parse("2020-01-18 19:26:00"), 
				Event.DATETIME_SDF.parse("2020-01-24 10:26:00"), "description_test");		
		request.setChallengeResponse(challengeResponse);	
		request.setEntity(gson.toJson(event, Event.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);

		assertEquals(200, jsonResponse.getStatus().getCode());
	}
	
	@Test
	/* title parameter empty space ---> VOID_CLASS_FIELD = 950 */
	public void testPost2() throws ParseException {
		Request request = new Request(Method.POST, url);
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, 
				"email_test", "password_test");
		Date startDate = Event.DATETIME_SDF.parse("2020-01-15 19:26:00");
		Date endDate = Event.DATETIME_SDF.parse("2020-01-16 10:26:00"); 
		Event event = new Event("", startDate, endDate, "description_test");
		request.setChallengeResponse(challengeResponse);	
		request.setEntity(gson.toJson(event, Event.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);

		assertEquals(950, jsonResponse.getStatus().getCode());
	}
	
	@Test
	/* startDate parameter null ---> VOID_CLASS_FIELD = 950 */
	public void testPost3() throws ParseException {
		Request request = new Request(Method.POST, url);
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, 
				"email_test", "password_test");
		Date startDate = null;
		Date endDate = Event.DATETIME_SDF.parse("2020-01-16 10:26:00"); 
		Event event = new Event("title_test", startDate, endDate, "description_test");
		request.setChallengeResponse(challengeResponse);	
		request.setEntity(gson.toJson(event, Event.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);

		assertEquals(950, jsonResponse.getStatus().getCode());
	}
	
	@Test
	/* endDate parameter null ---> VOID_CLASS_FIELD = 950 */
	public void testPost4() throws ParseException {
		Request request = new Request(Method.POST, url);
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, 
				"email_test", "password_test");
		Date startDate = Event.DATETIME_SDF.parse("2020-01-15 19:26:00");
		Date endDate = null; 
		Event event = new Event("title_test", startDate, endDate, "description_test");
		request.setChallengeResponse(challengeResponse);	
		request.setEntity(gson.toJson(event, Event.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);

		assertEquals(950, jsonResponse.getStatus().getCode());
	}
	
	@Test
	/* description parameter empty space ---> VOID_CLASS_FIELD = 950 */
	public void testPost5() throws ParseException {
		Request request = new Request(Method.POST, url);
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, 
				"email_test", "password_test");
		Date startDate = Event.DATETIME_SDF.parse("2020-01-15 19:26:00");
		Date endDate = Event.DATETIME_SDF.parse("2020-01-16 10:26:00"); 
		Event event = new Event("title_test", startDate, endDate, "");
		request.setChallengeResponse(challengeResponse);	
		request.setEntity(gson.toJson(event, Event.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);

		assertEquals(950, jsonResponse.getStatus().getCode());
	}
	
	/////////////////////////////////////////////PUT///////////////////////////////////////////////

	@Test
	/* update event: all parameters are valid  ---> 200 OK */
	public void testPut1() throws ParseException {
		Request request;
						
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, 
				"email_test", "password_test");
		
		Event event = new Event("title_test", Event.DATETIME_SDF.parse("2020-01-18 19:26:00"), 
				Event.DATETIME_SDF.parse("2020-01-24 10:26:00"), "description_test");	
		request = new Request(Method.POST, url);
		request.setChallengeResponse(challengeResponse);	
		request.setEntity(gson.toJson(event, Event.class), MediaType.APPLICATION_JSON);
		client.handle(request);
		
		request = new Request(Method.GET, url);
		Response jsonResponse1 = client.handle(request);
		Event[] events = gson.fromJson(jsonResponse1.getEntityAsText(), Event[].class);
		
		request = new Request(Method.PUT, url);
		request.setChallengeResponse(challengeResponse);
		Event event_update = events[0];
		event_update.setTitle("title_text_UPDATED");
		request.setEntity(gson.toJson(event_update, Event.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);
		
		assertEquals(200, jsonResponse.getStatus().getCode());
	}
	
	@Test
	/* update event: invalid id  ---> INVALID_EVENT_ID = 800 */
	public void testPut2() throws ParseException {
		Request request;
						
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, 
				"email_test", "password_test");
		
		Event event = new Event("title_test", Event.DATETIME_SDF.parse("2020-01-18 19:26:00"), 
				Event.DATETIME_SDF.parse("2020-01-24 10:26:00"), "description_test");	
		request = new Request(Method.POST, url);
		request.setChallengeResponse(challengeResponse);	
		request.setEntity(gson.toJson(event, Event.class), MediaType.APPLICATION_JSON);
		client.handle(request);
		
		request = new Request(Method.GET, url);
		Response jsonResponse1 = client.handle(request);
		Event[] events = gson.fromJson(jsonResponse1.getEntityAsText(), Event[].class);
		
		request = new Request(Method.PUT, url);
		request.setChallengeResponse(challengeResponse);
		Event event_update = events[0];
		event_update.setTitle("title_text_UPDATED");
		event_update.setId(-1);						// the db doesn't contain this id 	
		request.setEntity(gson.toJson(event_update, Event.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);
		
		assertEquals(800, jsonResponse.getStatus().getCode());
	}
	
	@Test
	/* update event: UNAUTHORIZED_USER = 901 */
	public void testPut3() throws ParseException {
		Request request;
						
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, 
				"email_test", "password_test");
		
		Event event = new Event("title_test", Event.DATETIME_SDF.parse("2020-01-18 19:26:00"), 
				Event.DATETIME_SDF.parse("2020-01-24 10:26:00"), "description_test");	
		request = new Request(Method.POST, url);
		request.setChallengeResponse(challengeResponse);	
		request.setEntity(gson.toJson(event, Event.class), MediaType.APPLICATION_JSON);
		client.handle(request);
		
		request = new Request(Method.GET, url);
		Response jsonResponse1 = client.handle(request);
		Event[] events = gson.fromJson(jsonResponse1.getEntityAsText(), Event[].class);
		
		request = new Request(Method.PUT, url);
		request.setChallengeResponse(challengeResponse);
		Event event_update = events[0];
		event_update.setOwnerEmail("email_test_invalid");
		
		event_update.setTitle("title_text_UPDATED");
		request.setEntity(gson.toJson(event_update, Event.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);
		
		assertEquals(901, jsonResponse.getStatus().getCode());
	}
}
