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

import commons.User;
import server.backend.DBManager;
import server.web.frontend.EventsRegistryWebApplication;

class UserTelegramJSONTest {

	private static Gson gson;
	private static String url = "http://localhost:8182/eventsRegistry/users";
	private Client client = new Client(Protocol.HTTP);
	
	@BeforeAll
	public static void setUpBeforeAll() throws Exception {				
		EventsRegistryWebApplication.main(null);
		gson = EventsRegistryWebApplication.GSON;

		DBManager.executeUpdate("delete from users;");
		
		// add a user for resource with guard		
		Client client = new Client(Protocol.HTTP);
		Request request = new Request(Method.POST, url);
		User user1 = new User("name_test_1", "surname_test_1", "email_test_1", "password_test_1", null);
		request.setEntity(gson.toJson(user1, User.class), MediaType.APPLICATION_JSON);
		client.handle(request);
		User user2 = new User("name_test_2", "surname_test_2", "email_test_2", "password_test_2", null);
		request.setEntity(gson.toJson(user2, User.class), MediaType.APPLICATION_JSON);
		client.handle(request);
	}
	
	@AfterAll
	public static void tearDownAfterClass() throws Exception {
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
	/* email parameter OK user logged and authorized ---> return: 200 OK, valid token*/
	public void testGet1() {
		String email = "email_test_1";
		Request request = new Request(Method.GET, url + "/" + email + "/telegram");
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, 
				"email_test_1", "password_test_1");
		
		request.setChallengeResponse(challengeResponse);	
		Response jsonResponse = client.handle(request);

		assertEquals(200, jsonResponse.getStatus().getCode());
	}
	
	@Test
	/* wrong user credentials ---> 401 */
	public void testGet2() {
		String email = "email_test";
		Request request = new Request(Method.GET, url + "/" + email + "/telegram");
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, 
				"email_test_1", "password_test_1_WRONG");
		
		request.setChallengeResponse(challengeResponse);	
		Response jsonResponse = client.handle(request);

		assertEquals(401, jsonResponse.getStatus().getCode());
	}
	
	@Test
	/* invalid email passed as attribute ---> INVALID_USER_EMAIL = 900 */
	public void testGet3() {
		String email = "email_test_INVALID";
		Request request = new Request(Method.GET, url + "/" + email + "/telegram");
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, 
				"email_test_1", "password_test_1");
		
		request.setChallengeResponse(challengeResponse);	
		Response jsonResponse = client.handle(request);

		assertEquals(900, jsonResponse.getStatus().getCode());
	}
	
	@Test
	/* email passed as attribute valid but not equals to email of logged user ---> UNAUTHORIZED_USER = 901 */
	public void testGet4() {
		String email = "email_test_2";
		Request request = new Request(Method.GET, url + "/" + email + "/telegram");
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, 
				"email_test_1", "password_test_1");
		
		request.setChallengeResponse(challengeResponse);	
		Response jsonResponse = client.handle(request);

		assertEquals(901, jsonResponse.getStatus().getCode());
	}

}
