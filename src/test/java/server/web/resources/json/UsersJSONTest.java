package server.web.resources.json;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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

public class UsersJSONTest {

	private static Gson gson = new Gson();
	
	@BeforeAll
	public static void setUpBeforeAll() throws Exception {				
		LaunchServerApp.execute();
	}
	
	@AfterAll
	public static void tearDownAfterClass() throws Exception {
		DBManager.executeUpdate("delete from users;");
	}
	
	@BeforeEach
	public void setUp() throws Exception {
		DBManager.executeUpdate("delete from users;");
	}
	
	@AfterEach
	public void tearDown() throws Exception {
		DBManager.executeUpdate("delete from users;");
	}
	
	/* è corretto far restituire una lista utenti ad un utente loggato (?) */
	@Test
	public void testGet1() {
		String url = "http://localhost:8182/eventsRegistry/users";
		Client client = new Client(Protocol.HTTP);
		Request request = new Request(Method.GET, url);

		Response jsonResponse = client.handle(request);
		Assertions.assertEquals(200, jsonResponse.getStatus().getCode());
	}

	/* è corretto far restituire una lista utenti ad un utente loggato (?) */
	@Test
	/* users size > 0 */
	public void testGet2() {
		String url = "http://localhost:8182/eventsRegistry/users";
		Client client = new Client(Protocol.HTTP);
		Request request = new Request(Method.POST, url);
		User user = new User("name_test", "surname_test", "email_test@gmail.com", "password_test", null);
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		client.handle(request);

		client = new Client(Protocol.HTTP);
		request = new Request(Method.GET, url);

		Response jsonResponse = client.handle(request);
		Assertions.assertEquals(200, jsonResponse.getStatus().getCode());
	}

	/////////////////////////////////////////POST/////////////////////////////////////////////////////

	@Test
	/* adding a user to app */
	public void testPost1() {
		String url = "http://localhost:8182/eventsRegistry/users";
		Client client = new Client(Protocol.HTTP);
		Request request = new Request(Method.POST, url);
		User user = new User("name_test", "surname_test", "email_test@gmail.com", "password_test", null);
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);

		Assertions.assertEquals(true, gson.fromJson(jsonResponse.getEntityAsText(), boolean.class));
	}
	
	@Test
	/* adding two user with same credentials */
	public void testPost2() {
		String url = "http://localhost:8182/eventsRegistry/users";
		Client client = new Client(Protocol.HTTP);
		Request request = new Request(Method.POST, url);
		User user = new User("name_test", "surname_test", "email_test@gmail.com", "password_test", null);
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		client.handle(request);

		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		Response jsonResponse2 = client.handle(request);
		
		Assertions.assertEquals(900, jsonResponse2.getStatus().getCode());
	}
	
	@Test
	/* adding user with wrong credentials: empty space as email*/
	public void testPost3() {
		String url = "http://localhost:8182/eventsRegistry/users";
		Client client = new Client(Protocol.HTTP);
		Request request = new Request(Method.POST, url);
		User user = new User("name_test", "surname_test", "", "password_test", null);
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);
		
		Assertions.assertEquals(950, jsonResponse.getStatus().getCode());
	}
	
	@Test
	/* adding user with wrong credentials: empty space as name*/
	public void testPost4() {
		String url = "http://localhost:8182/eventsRegistry/users";
		Client client = new Client(Protocol.HTTP);
		Request request = new Request(Method.POST, url);
		User user = new User("", "surname_test", "email_test@gmail.com", "password_test", null);
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);
		
		Assertions.assertEquals(950, jsonResponse.getStatus().getCode());
	}
	
	@Test
	/* adding user with wrong credentials: empty space as surname*/
	public void testPost5() {
		String url = "http://localhost:8182/eventsRegistry/users";
		Client client = new Client(Protocol.HTTP);
		Request request = new Request(Method.POST, url);
		User user = new User("name_test", "", "email_test@gmail.com", "password_test", null);
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);
		
		Assertions.assertEquals(950, jsonResponse.getStatus().getCode());
	}
	
	@Test
	/* adding user with wrong credentials: empty space as password*/
	public void testPost6() {
		String url = "http://localhost:8182/eventsRegistry/users";
		Client client = new Client(Protocol.HTTP);
		Request request = new Request(Method.POST, url);
		User user = new User("name_test", "surname_test",
				"email_test_wrong_cr@gmail.com", "", null);
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);
		
		Assertions.assertEquals(950, jsonResponse.getStatus().getCode());
	}
	
	@Test
	/* update user -> post a user object, then update it  - only email parameter don't change for update */
	public void testPut1() {
		String url = "http://localhost:8182/eventsRegistry/users";
		Client client = new Client(Protocol.HTTP);
		Request request = new Request(Method.POST, url);
		User user = new User("name_test", "surname_test", "email_test@gmail.com", "password_test", null);
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		client.handle(request);
				
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, 
				"email_test@gmail.com", "password_test");
		request = new Request(Method.PUT, url);
		user = new User("name_test_UPDATE", "surname_test_UPDATE", "email_test@gmail.com", "password_test_UPDATE", null);
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		request.setChallengeResponse(challengeResponse);
		Response jsonResponse = client.handle(request);

		Assertions.assertEquals(true, gson.fromJson(jsonResponse.getEntityAsText(), boolean.class));
	}
	
	@Test
	/* update user -> post a user object, then update it  - only email parameter don't change for update
	 * + name empty ---> VOID_CLASS_FIELD = 950 */
	public void testPut2() {
		String url = "http://localhost:8182/eventsRegistry/users";
		Client client = new Client(Protocol.HTTP);
		
		Request request = new Request(Method.POST, url);
		User user = new User("name_test", "surname_test", "email_test@gmail.com", "password_test", null);
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		client.handle(request);
		
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, 
				"email_test@gmail.com", "password_test");
		request = new Request(Method.PUT, url);
		request.setChallengeResponse(challengeResponse);
		user = new User("", "surname_test_UPDATE", "email_test@gmail.com", "password_test_UPDATE", null);
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);
		
		Assertions.assertEquals(950, jsonResponse.getStatus().getCode());
	}
	
	@Test
	/* update user -> post a user object, then update it  - only email parameter don't change for update
	 * + surname empty ---> VOID_CLASS_FIELD = 950 */
	public void testPut3() {
		String url = "http://localhost:8182/eventsRegistry/users";
		Client client = new Client(Protocol.HTTP);
		
		Request request = new Request(Method.POST, url);
		User user = new User("name_test", "surname_test", "email_test@gmail.com", "password_test", null);
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		client.handle(request);
		
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, 
				"email_test@gmail.com", "password_test");
		request = new Request(Method.PUT, url);
		request.setChallengeResponse(challengeResponse);
		user = new User("name_test_UPDATE", "", "email_test@gmail.com", "password_test_UPDATE", null);
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);
		
		Assertions.assertEquals(950, jsonResponse.getStatus().getCode());
	}
	
	@Test
	/* update user -> post a user object, then update it  - only email parameter don't change for update 
	 * + password empty ---> VOID_CLASS_FIELD = 950 */
	public void testPut4() {
		String url = "http://localhost:8182/eventsRegistry/users";
		Client client = new Client(Protocol.HTTP);
		
		Request request = new Request(Method.POST, url);
		User user = new User("name_test", "surname_test", "email_test@gmail.com", "password_test", null);
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		client.handle(request);
		
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, 
				"email_test@gmail.com", "password_test");
		request = new Request(Method.PUT, url);
		request.setChallengeResponse(challengeResponse);		
		user = new User("name_test_UPDATE", "surname_test_UPDATE", "email_test@gmail.com", "", null);
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);
		
		Assertions.assertEquals(950, jsonResponse.getStatus().getCode());
	}
		
	@Test
	/* update user -> empty space for email parameter, user param email changed ---> UNAUTHORIZED_USER = 901*/
	public void testPut6() {
		String url = "http://localhost:8182/eventsRegistry/users";
		Client client = new Client(Protocol.HTTP);
		
		Request request = new Request(Method.POST, url);
		User user = new User("name_test", "surname_test", "email_test@gmail.com", "password_test", null);
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		client.handle(request);
		
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, 
				"email_test@gmail.com", "password_test");
		
		request = new Request(Method.PUT, url);
		request.setChallengeResponse(challengeResponse);		
		user = new User("name_test_UPDATE", "surname_test_UPDATE", "", "password_test", null);
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);
		
		Assertions.assertEquals(901, jsonResponse.getStatus().getCode());
	}
	
	@Test
	/* update user -> empty space for email parameter, user param email changed ---> UNAUTHORIZED_USER = 901*/
	public void testPut7() {
		String url = "http://localhost:8182/eventsRegistry/users";
		Client client = new Client(Protocol.HTTP);
		
		Request request = new Request(Method.POST, url);
		User user = new User("name_test", "surname_test", "email_test@gmail.com", "password_test", null);
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		client.handle(request);
		
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, 
				"email_test@gmail.com", "password_test_WRONG");
		
		request = new Request(Method.PUT, url);
		request.setChallengeResponse(challengeResponse);		
		user = new User("name_test_UPDATE", "surname_test_UPDATE", "", "password_test", null);
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);
		
	    Assertions.assertEquals(401, jsonResponse.getStatus().getCode());
	}

}
