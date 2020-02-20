package server.web.resources.json;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import commons.Event;
import commons.User;
import commons.exceptions.GenericSQLException;
import commons.exceptions.InvalidEventIdException;
import commons.exceptions.VoidClassFieldException;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.*;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.ResourceException;
import server.backend.DBManager;
import server.backend.EventsAccessObject;
import server.web.frontend.EventsRegistryWebApplication;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

class EventPhotoJSONTest {

	private static String url = "http://localhost:8182/eventsRegistry/events";
	private static Gson gson;
	private static Client client;
	private static int eventId_1;
	private static int eventId_2;
	private static int eventId_3;
	private static String photoName1; // naming appointed by server resource
	private static String photoName2;
	private static String photoName3;
	private static String photoPath1;
	private static String photoPath2;
	private static File source = new File("email_test@gmail.com.jpg");
	private static String email = "email_test@gmail.com";
	private static String password = "password_test";

	@BeforeAll
	public static void setUpBeforeAll() throws Exception {
		LaunchServerApp.execute();
		gson = EventsRegistryWebApplication.GSON;
		client = new Client(Protocol.HTTP);

		DBManager.executeUpdate("delete from events_users_participations;");
		DBManager.executeUpdate("delete from events;");
		DBManager.executeUpdate("delete from users;");

		FileUtils.cleanDirectory(new File(EventsRegistryWebApplication.EVENTS_PHOTOS_DIRECTORY));
		//for the second event I dont copy source image
		//photoPath2 = EventsRegistryWebApplication.EVENTS_PHOTOS_DIRECTORY + photoName2;

		preFillDb();
	}

	public static void preFillDb() throws InvalidEventIdException, VoidClassFieldException, GenericSQLException, ParseException, IOException {
		// add a user for resource with guard
		String url_users = "http://localhost:8182/eventsRegistry/users";
		Request request = new Request(Method.POST, url_users);
		User user = new User("name_test", "surname_test", email, password, "email_test.jpg");
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		client.handle(request);


		String url_events = "http://localhost:8182/eventsRegistry/events";
		Request request_events = new Request(Method.POST, url_events);
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC,
				email, password);
		request_events.setChallengeResponse(challengeResponse);

		//adding an event to attach a photo
		Event event1 = new Event("title_test_1", Event.DATETIME_SDF.parse("2020-01-18 19:26:00"),
				Event.DATETIME_SDF.parse("2020-01-24 10:26:00"), "description_test");
		event1.setOwnerEmail(user.getEmail());
		request_events.setEntity(gson.toJson(event1, Event.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request_events);
		System.out.println(jsonResponse.getEntityAsText());
		eventId_1 = gson.fromJson(jsonResponse.getEntityAsText(), int.class);
		photoName1 = eventId_1+".jpg";
		photoPath1 = EventsRegistryWebApplication.EVENTS_PHOTOS_DIRECTORY + photoName1;
		FileUtils.copyFile(source, new File(photoPath1));

		//adding an event to attach a photo - event2 never has any pictures attached
		Event event2 = new Event("title_test_2", Event.DATETIME_SDF.parse("2020-01-18 19:26:00"),
				Event.DATETIME_SDF.parse("2020-01-24 10:26:00"), "description_test");
		event2.setOwnerEmail(user.getEmail());
		request_events.setEntity(gson.toJson(event2, Event.class), MediaType.APPLICATION_JSON);
		jsonResponse = client.handle(request_events);
		eventId_2 = gson.fromJson(jsonResponse.getEntityAsText(), int.class);

		//adding an event to attach a photo - we use this event3 for the put request
		Event event3 = new Event("title_test_3", Event.DATETIME_SDF.parse("2020-01-18 19:26:00"),
				Event.DATETIME_SDF.parse("2020-01-24 10:26:00"), "description_test");
		event3.setOwnerEmail(user.getEmail());
		request_events.setEntity(gson.toJson(event3, Event.class), MediaType.APPLICATION_JSON);
		jsonResponse = client.handle(request_events);
		eventId_3 = gson.fromJson(jsonResponse.getEntityAsText(), int.class);
	}

	@AfterAll
	public static void tearDownAfterClass() throws Exception {
		DBManager.executeUpdate("delete from events_users_participations;");
		DBManager.executeUpdate("delete from events;");
		DBManager.executeUpdate("delete from users;");
	}

	@BeforeEach
	public void setUp() throws Exception {
	}

	@AfterEach
	public void tearDown() throws Exception {
	}

	/////////////////////////////////////////////GET///////////////////////////////////////////////

	@Test
	/* foto presente nel DB */
	public void testGet1() throws IOException {
		Request request = new Request(Method.GET, url + "/" + eventId_1 + "/photo");

		Response jsonResponse = client.handle(request);

		assertEquals(200, jsonResponse.getStatus().getCode());
	}

	@Test
	/* foto non presente nel DB ---> 204 - NO CONTENT */
	public void testGet2() throws IOException {
		Request request = new Request(Method.GET, url + "/" + eventId_2 + "/photo");

		Response jsonResponse = client.handle(request);

		assertEquals(204, jsonResponse.getStatus().getCode());
	}

	/////////////////////////////////////////////PUT///////////////////////////////////////////////

//	@Test
//	/* all parameters ok ---> 200 OK */
//	public void put1() throws ResourceException, IOException {
////		Files.deleteIfExists(Paths.get(photoPath3));
//		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC,
//				email, password);
//		String URI = url  + "/" + eventId_3 + "/photo";
//		Request request = new Request(Method.PUT, URI);
//		FileRepresentation payload = new FileRepresentation(source,
//				MediaType.IMAGE_JPEG);
//		request.setEntity(payload);
//		request.setChallengeResponse(challengeResponse);
//		Response response = client.handle(request);
//
//		Assertions.assertEquals(200, response.getStatus().getCode());
//	}

//	@Test
//	/* set a MediaType different of JPEG ---> 415 Unsupported Media Type */
//	public void put2() throws ResourceException, IOException {
//		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC,
//				email, password);
//		String URI = url  + "/" + eventId_2 + "/photo";
//		Request request = new Request(Method.PUT, URI);
//		FileRepresentation payload = new FileRepresentation(source,
//				MediaType.IMAGE_GIF);
//		request.setEntity(payload);
//		request.setChallengeResponse(challengeResponse);
//		Response response = client.handle(request);
//
//		Assertions.assertEquals(415, response.getStatus().getCode());
//	}
}
