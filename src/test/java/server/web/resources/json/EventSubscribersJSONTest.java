package server.web.resources.json;

import static org.junit.jupiter.api.Assertions.*;

import commons.exceptions.*;
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
import server.backend.DBManager;
import server.backend.EventsAccessObject;
import server.backend.UsersAccessObject;
import server.web.frontend.EventsRegistryWebApplication;

import java.sql.SQLException;
import java.text.ParseException;

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

		preFillDb();
	}

	public static void preFillDb() throws InvalidEventIdException, VoidClassFieldException, GenericSQLException, ParseException {
		// add a user for resource with guard
		String url_users = "http://localhost:8182/eventsRegistry/users";
		Request request = new Request(Method.POST, url_users);
		User user = new User("name_test", "surname_test", "email_test@gmail.com", "password_test", "email_test.jpg");
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		client.handle(request);

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

	@Test
	/* INVALID_EVENT_ID = 800 */
	public void testGet2() {
		Request request = new Request(Method.GET, url + "/" + 0 + "/subscribers");
		Response jsonResponse = client.handle(request);

		assertEquals(800, jsonResponse.getStatus().getCode());
	}

	@Test
	/* VOID_CLASS_FIELD = 950 */
	public void testGet3() {
		Request request = new Request(Method.GET, url + "/" + -1 + "/subscribers");
		Response jsonResponse = client.handle(request);

		assertEquals(950, jsonResponse.getStatus().getCode());
	}

	@Test
	public void testGet4() {
		this.testPost1();
		Request request = new Request(Method.GET, url + "/" + eventId + "/subscribers");
		Response jsonResponse = client.handle(request);

		assertEquals(200, jsonResponse.getStatus().getCode());
	}

	@Test
	/*  delete EventsRegistry's DB ---> GENERIC_SQL = 951 */
	public void testGet5() throws SQLException, VoidClassFieldException, InvalidEventIdException, GenericSQLException, ParseException {
		DBManager.executeUpdate("drop database events_registry;");
		Request request = new Request(Method.GET, url + "/" + eventId + "/subscribers");
		Response jsonResponse = client.handle(request);

		assertEquals(ErrorCodes.GENERIC_SQL, jsonResponse.getStatus().getCode());
		DBManager.createDB();
		preFillDb();
	}

	//////////////////////////////////////////////POST/////////////////////////////////////////////

	@Test
	/* utente not subscribered yet ---> 200 OK */
	public void testPost1() {
		Request request = new Request(Method.POST, url + "/" + eventId + "/subscribers");
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC,
				"email_test@gmail.com", "password_test");
		String email = "email_test@gmail.com";
		request.setChallengeResponse(challengeResponse);
		request.setEntity(gson.toJson(email, String.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);

		assertEquals(200, jsonResponse.getStatus().getCode());
	}

	@Test
	/* utente subscribered yet ---> GENERIC_SQL = 951 */
	public void testPost2() throws InvalidUserEmailException, GenericSQLException, VoidClassFieldException, InvalidEventIdException {
		String email = "email_test@gmail.com";

		// precondition
		EventsAccessObject.addEventSubscriber(eventId, email);

		Request request = new Request(Method.POST, url + "/" + eventId + "/subscribers");
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC,
				"email_test@gmail.com", "password_test");
		request.setChallengeResponse(challengeResponse);
		request.setEntity(gson.toJson(email, String.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);

		assertEquals(951, jsonResponse.getStatus().getCode());
	}

	@Test
	/* INVALID_EVENT_ID = 800 */
	public void testPost3() {
		Request request = new Request(Method.POST, url + "/" + 0 + "/subscribers");
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC,
				"email_test@gmail.com", "password_test");
		String email = "email_test@gmail.com";
		request.setChallengeResponse(challengeResponse);
		request.setEntity(gson.toJson(email, String.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);

		assertEquals(800, jsonResponse.getStatus().getCode());
	}

	@Test
	/* VOID_CLASS_FIELD = 950 */
	public void testPost4() {
		Request request = new Request(Method.POST, url + "/" + -1 + "/subscribers");
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC,
				"email_test@gmail.com", "password_test");
		String email = "email_test@gmail.com";
		request.setChallengeResponse(challengeResponse);
		request.setEntity(gson.toJson(email, String.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);

		assertEquals(950, jsonResponse.getStatus().getCode());
	}

	@Test
	/* UNAUTHORIZED_USER = 901 */
	public void testPost5() {
		Request request = new Request(Method.POST, url + "/" + -1 + "/subscribers");
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC,
				"email_test@gmail.com", "password_test");
		String email = "email_test_UNEQUAL@gmail.com";
		request.setChallengeResponse(challengeResponse);
		request.setEntity(gson.toJson(email, String.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);

		assertEquals(901, jsonResponse.getStatus().getCode());
	}

	@Test
	/*  delete EventsRegistry's DB ---> GENERIC_SQL = 951 */
	public void testPost6() throws SQLException, VoidClassFieldException, InvalidEventIdException, GenericSQLException, ParseException {
		Request request = new Request(Method.POST, url + "/" + eventId + "/subscribers");
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC,
				"email_test@gmail.com", "password_test");
		String email = "email_test@gmail.com";
		request.setChallengeResponse(challengeResponse);
		request.setEntity(gson.toJson(email, String.class), MediaType.APPLICATION_JSON);
		DBManager.executeUpdate("drop database events_registry;");
		Response jsonResponse = client.handle(request);

		assertEquals(ErrorCodes.GENERIC_SQL, jsonResponse.getStatus().getCode());
		DBManager.createDB();
		preFillDb();
	}

	/////////////////////////////////////////////DELETE//////////////////////////////////////////////////////

	@Test
	/* utente not subscribered yet ---> INVALID_PARTICIPATION = 956 */
	public void testDelete1() {
		Request request = new Request(Method.DELETE, url + "/" + eventId + "/subscribers");
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC,
				"email_test@gmail.com", "password_test");
		String email = "email_test@gmail.com";
		request.setChallengeResponse(challengeResponse);
		request.setEntity(gson.toJson(email, String.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);

		assertEquals(ErrorCodes.INVALID_PARTICIPATION, jsonResponse.getStatus().getCode());
	}

	@Test
	/* utente subscribered yet ---> 200 OK */
	public void testDelete2() throws InvalidUserEmailException, GenericSQLException, VoidClassFieldException, InvalidEventIdException {
		String email = "email_test@gmail.com";

		// precondition
		EventsAccessObject.addEventSubscriber(eventId, email);

		Request request = new Request(Method.DELETE, url + "/" + eventId + "/subscribers");
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC,
				"email_test@gmail.com", "password_test");
		request.setChallengeResponse(challengeResponse);
		request.setEntity(gson.toJson(email, String.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);

		assertEquals(200, jsonResponse.getStatus().getCode());
	}

	@Test
	/* email NULL ---> VOID_CLASS_FIELD = 950 */
	public void testDelete3() {
		Request request = new Request(Method.DELETE, url + "/" + eventId + "/subscribers");
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC,
				"email_test@gmail.com", "password_test");
		String email = null;
		request.setChallengeResponse(challengeResponse);
		request.setEntity(gson.toJson(email, String.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);

		assertEquals(950, jsonResponse.getStatus().getCode());
	}

	@Test
	/* UNAUTHORIZED_USER = 901 */
	public void testDelete4() {
		Request request = new Request(Method.DELETE, url + "/" + eventId + "/subscribers");
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC,
				"email_test@gmail.com", "password_test");
		String email = "email_test_UNEQUAL@gmail.com";
		request.setChallengeResponse(challengeResponse);
		request.setEntity(gson.toJson(email, String.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);

		assertEquals(901, jsonResponse.getStatus().getCode());
	}

	@Test
	/* INVALID_EVENT_ID = 800 */
	public void testDelete5() {
		Request request = new Request(Method.DELETE, url + "/" + 0 + "/subscribers");
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC,
				"email_test@gmail.com", "password_test");
		String email = "email_test@gmail.com";
		request.setChallengeResponse(challengeResponse);
		request.setEntity(gson.toJson(email, String.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);

		assertEquals(800, jsonResponse.getStatus().getCode());
	}

	@Test
	/* VOID_CLASS_FIELD = 950 */
	public void testDelete6() {
		Request request = new Request(Method.DELETE, url + "/" + -1 + "/subscribers");
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC,
				"email_test@gmail.com", "password_test");
		String email = "email_test@gmail.com";
		request.setChallengeResponse(challengeResponse);
		request.setEntity(gson.toJson(email, String.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);

		assertEquals(ErrorCodes.VOID_CLASS_FIELD, jsonResponse.getStatus().getCode());
	}

	@Test
	/*  delete EventsRegistry's DB ---> GENERIC_SQL = 951 */
	public void testDelete7() throws InvalidUserEmailException, GenericSQLException, VoidClassFieldException, InvalidEventIdException, ParseException, SQLException {
		String email = "email_test@gmail.com";

		// precondition
		EventsAccessObject.addEventSubscriber(eventId, email);

		Request request = new Request(Method.DELETE, url + "/" + eventId + "/subscribers");
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC,
				"email_test@gmail.com", "password_test");
		request.setChallengeResponse(challengeResponse);
		request.setEntity(gson.toJson(email, String.class), MediaType.APPLICATION_JSON);

		DBManager.executeUpdate("drop database events_registry;");
		Response jsonResponse = client.handle(request);
		assertEquals(ErrorCodes.GENERIC_SQL, jsonResponse.getStatus().getCode());
		DBManager.createDB();
		preFillDb();
	}
}
