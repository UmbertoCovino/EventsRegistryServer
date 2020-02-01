package server.web.resources.json;

import static org.junit.jupiter.api.Assertions.*;

import commons.exceptions.ErrorCodes;
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

import commons.User;
import server.backend.DBManager;
import server.web.frontend.EventsRegistryWebApplication;

import java.sql.SQLException;

class UserJSONTest {

	private static Gson gson = new Gson();
	private static Client client;
	private static String url = "http://localhost:8182/eventsRegistry/users/";
	private static User user;

	@BeforeAll
	public static void setUpBeforeAll() throws Exception {				
		LaunchServerApp.execute();
	}
	
	@AfterAll
	public static void tearDownAfterClass() throws Exception {
	}
	
	@BeforeEach
	public void setUp() throws Exception {
	}
	
	@AfterEach
	public void tearDown() throws Exception {
	}
	
	/////////////////////////////////////////GET////////////////////////////////////////////////////////////
	
	@Test
	/* non manca una guard? E' possibile ottenere un oggetto User solo grazie alla email? 
	 * Sì perché mi restituisce un oggetto User senza password */
	public void testGet1() {
		String url_users = "http://localhost:8182/eventsRegistry/users";
		client = new Client(Protocol.HTTP);
		Request request = new Request(Method.POST, url_users);
		user = new User("name_test", "surname_test", "email_test@gmail.com", "password_test", "email_test.jpg");
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		client.handle(request);

		request = new Request(Method.GET, url + user.getEmail());
		Response jsonResponse =  client.handle(request);
		
		User user = new User("name_test", "surname_test", "email_test@gmail.com", "email_test@gmail.com.jpg");
		User got_user = gson.fromJson(jsonResponse.getEntityAsText(), User.class);
		
		assertEquals(user.toString(), got_user.toString()); 
		//metodi alternativi per verificare l'equivalenza tra due oggetti User direttamente???
		//*non usando quindi il toString()
	}
	
	@Test
	/* wrong parameter: invalid attribute email ---> INVALID_USER_EMAIL = 900 */
	public void testGet2() {
		String url_users = "http://localhost:8182/eventsRegistry/users";
		client = new Client(Protocol.HTTP);
		Request request = new Request(Method.POST, url_users);
		user = new User("name_test", "surname_test", "email_test@gmail.com", "password_test", "email_test.jpg");
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		client.handle(request);

		request = new Request(Method.GET, url + "email_test_INVALID@gmail.com");
		Response jsonResponse =  client.handle(request);
		
		assertEquals(900, jsonResponse.getStatus().getCode());
	}

	@Test
	/* VOID_CLASS_FIELD = 950 */
	public void testGet3() {
		String url_users = "http://localhost:8182/eventsRegistry/users";
		client = new Client(Protocol.HTTP);
		Request request = new Request(Method.POST, url_users);
		user = new User("name_test", "surname_test", "email_test@gmail.com",
				"password_test", "email_test.jpg");
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		client.handle(request);

		request = new Request(Method.GET, url + "invalid_email_void_parameter");
		Response jsonResponse =  client.handle(request);

		assertEquals(950, jsonResponse.getStatus().getCode());
	}

	@Test
	/*  delete EventsRegistry's DB ---> GENERIC_SQL = 951 */
	public void testGet4() throws SQLException {
		String url_users = "http://localhost:8182/eventsRegistry/users";
		client = new Client(Protocol.HTTP);
		Request request = new Request(Method.POST, url_users);
		user = new User("name_test", "surname_test", "email_test@gmail.com", "password_test", "email_test.jpg");
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		client.handle(request);

		DBManager.executeUpdate("drop database events_registry;");
		request = new Request(Method.GET, url + user.getEmail());
		Response jsonResponse =  client.handle(request);

		assertEquals(ErrorCodes.GENERIC_SQL, jsonResponse.getStatus().getCode());
		DBManager.createDB();
	}
	
	///////////////////////////////////////////POST//////////////////////////////////////////////////////
	
	// non mi è chiara la funzione del POST nella resource class UserJson 
	// va rivisto questo metodo: getIfPasswordIsCorrect, eventualmente potrebbe essere usata la guard ed evitare la verifica
	
	//////////////////////////////////////////DELETE////////////////////////////////////////////////////
	
	@Test
	/* authorized user (email_logged_user equals USER_email_passed) ---> 200 OK */
	public void delete1() {
		String url_users = "http://localhost:8182/eventsRegistry/users";
		client = new Client(Protocol.HTTP);
		Request request = new Request(Method.POST, url_users);
		user = new User("name_test", "surname_test", "email_test@gmail.com", "password_test", "email_test.jpg");
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		client.handle(request);
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC,
				"email_test@gmail.com", "password_test");

		request = new Request(Method.DELETE, url + "email_test@gmail.com");
		request.setChallengeResponse(challengeResponse);
		Response jsonResponse = client.handle(request);
		
		assertEquals("User with email " + "email_test@gmail.com" + " removed.", gson.fromJson(jsonResponse.getEntityAsText(), String.class));
	}

	@Test
	/* UNAUTHORIZED_USER = 901 */
	public void delete2() {
		String url_users = "http://localhost:8182/eventsRegistry/users";
		client = new Client(Protocol.HTTP);
		Request request = new Request(Method.POST, url_users);
		user = new User("name_test", "surname_test", "email_test@gmail.com", "password_test", "email_test.jpg");
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		client.handle(request);
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC,
				"email_test@gmail.com", "password_test");

		request = new Request(Method.DELETE, url + "email_test_UNEQUAL@gmail.com");
		request.setChallengeResponse(challengeResponse);
		Response jsonResponse = client.handle(request);

		assertEquals(901, jsonResponse.getStatus().getCode());
	}

	@Test
	/* UNAUTHORIZED_USER = 901 */
	public void delete3() {
		String url_users = "http://localhost:8182/eventsRegistry/users";
		client = new Client(Protocol.HTTP);
		Request request = new Request(Method.POST, url_users);
		user = new User("name_test", "surname_test", "email_test@gmail.com", "password_test", "email_test.jpg");
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		client.handle(request);
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC,
				"email_test@gmail.com", "password_test");

		request = new Request(Method.DELETE, url + "email_test_UNEQUAL@gmail.com");
		request.setChallengeResponse(challengeResponse);
		Response jsonResponse = client.handle(request);

		assertEquals(901, jsonResponse.getStatus().getCode());
	}

	@Test
	/* authorized user (email_logged_user equals USER_email_passed)
		but delete EventsRegistry's DB ---> GENERIC_SQL = 951 */
	public void delete4() throws SQLException {
		String url_users = "http://localhost:8182/eventsRegistry/users";
		client = new Client(Protocol.HTTP);
		Request request = new Request(Method.POST, url_users);
		user = new User("name_test", "surname_test", "email_test@gmail.com", "password_test", "email_test.jpg");
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		client.handle(request);
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC,
				"email_test@gmail.com", "password_test");

		DBManager.executeUpdate("drop database events_registry;");
		request = new Request(Method.DELETE, url + "email_test@gmail.com");
		request.setChallengeResponse(challengeResponse);
		Response jsonResponse = client.handle(request);
		assertEquals(ErrorCodes.GENERIC_SQL, jsonResponse.getStatus().getCode());
		DBManager.createDB();
	}
}
