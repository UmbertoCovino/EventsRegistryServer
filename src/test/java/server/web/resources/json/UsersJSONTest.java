package server.web.resources.json;

import commons.exceptions.ErrorCodes;
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

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UsersJSONTest {

	private static Gson gson = new Gson();
	private static String url = "http://localhost:8182/eventsRegistry/users";
	private static Client client = new Client(Protocol.HTTP);

	
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
		Request request = new Request(Method.GET, url);

		Response jsonResponse = client.handle(request);
		Assertions.assertEquals(200, jsonResponse.getStatus().getCode());
	}

	/* è corretto far restituire una lista utenti ad un utente loggato (?) */
	@Test
	/* users size > 0 */
	public void testGet2() {
		Request request = new Request(Method.POST, url);
		User user = new User("name_test", "surname_test", "email_test@gmail.com", "password_test", null);
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		client.handle(request);

		client = new Client(Protocol.HTTP);
		request = new Request(Method.GET, url);

		Response jsonResponse = client.handle(request);
		Assertions.assertEquals(200, jsonResponse.getStatus().getCode());
	}

	@Test
	/*  delete EventsRegistry's DB ---> GENERIC_SQL = 951 */
	public void testGet3() throws SQLException {
		Request request = new Request(Method.GET, url);

		DBManager.executeUpdate("drop database events_registry;");
		Response jsonResponse = client.handle(request);
		assertEquals(ErrorCodes.GENERIC_SQL, jsonResponse.getStatus().getCode());
		DBManager.createDB();
	}

	/////////////////////////////////////////POST/////////////////////////////////////////////////////

	@Test
	/* adding a user to app */
	public void testPost1() {
		Request request = new Request(Method.POST, url);
		User user = new User("name_test", "surname_test", "email_test@gmail.com", "password_test", null);
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);

		Assertions.assertEquals(true, gson.fromJson(jsonResponse.getEntityAsText(), boolean.class));
	}
	
	@Test
	/* adding two user with same credentials */
	public void testPost2() {
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
		Response jsonResponse = this.addUserToServer("name_test", "surname_test", "", "password_test");

		Assertions.assertEquals(950, jsonResponse.getStatus().getCode());
	}
	
	@Test
	/* adding user with wrong credentials: empty space as name*/
	public void testPost4() {
		Response jsonResponse = addUserToServer("", "surname_test",
				"email_test@gmail.com", "password_test");
		
		Assertions.assertEquals(950, jsonResponse.getStatus().getCode());
	}
	
	@Test
	/* adding user with wrong credentials: empty space as surname*/
	public void testPost5() {
		Response jsonResponse = addUserToServer("name_test", "",
				"email_test@gmail.com", "password_test");

		Assertions.assertEquals(950, jsonResponse.getStatus().getCode());
	}
	
	@Test
	/* adding user with wrong credentials: empty space as password*/
	public void testPost6() {
		Response jsonResponse = addUserToServer("name_test", "surname_test",
				"email_test_wrong_cr@gmail.com", "");
		
		Assertions.assertEquals(950, jsonResponse.getStatus().getCode());
	}

	@Test
	/* NULL email parameter*/
	public void testPost8() {
		Response jsonResponse = addUserToServer("name_test", "surname_test",
				null, "password_test");

		Assertions.assertEquals(950, jsonResponse.getStatus().getCode());
	}

	@Test
	/* email length > 80*/
	public void testPost9() {
		Response jsonResponse = addUserToServer("name_test", "surname_test",
				"email_length_is_greater_than_eighty_so_the_test_case_code_is_950" +
						"email_length_is_greater_than_eighty_so_the_test_case_code_is_950" +
						"@gmail.com",
				"password_test");

		Assertions.assertEquals(950, jsonResponse.getStatus().getCode());
	}

	@Test
	/* not email is valid*/
	public void testPost10() {
		Response jsonResponse = addUserToServer("name_test", "surname_test",
				"INVALID_FORMAT_EMAIL", "password_test");

		Assertions.assertEquals(950, jsonResponse.getStatus().getCode());
	}

	@Test
	/* NULL name parameter */
	public void testPost11() {
		Response jsonResponse = addUserToServer(null, "surname_test",
				"email_test@gmail.com", "password_test");

		Assertions.assertEquals(950, jsonResponse.getStatus().getCode());
	}

	@Test
	/* NULL surname parameter */
	public void testPost12() {
		Response jsonResponse = addUserToServer("name_test", null,
				"email_test@gmail.com", "password_test");

		Assertions.assertEquals(950, jsonResponse.getStatus().getCode());
	}

	@Test
	/* name length > 80 */
	public void testPost13() {
		Response jsonResponse = addUserToServer(
				"name_length_greater_than_eighty_name_length_greater_than_eighty_" +
						"name_length_greater_than_eighty_name_length_greater_than_eighty",
				"surname_test",
				"email_test@gmail.com", "password_test");

		Assertions.assertEquals(950, jsonResponse.getStatus().getCode());
	}

	@Test
	/* surname length > 80 */
	public void testPost14() {
		Response jsonResponse = addUserToServer(
				"name_test", "surname_test_greater_than_eigthy_surname_test_" +
						"greater_than_eigthy_surname_test_greater_than_eigthy_surname_test_" +
						"greater_than_eigthy",
				"email_test@gmail.com", "password_test");

		Assertions.assertEquals(950, jsonResponse.getStatus().getCode());
	}

	@Test
	/* password length > 20 */
	public void testPost15() {
		Response jsonResponse = addUserToServer(
				"name_test", "surname_test",
				"email_test@gmail.com", "password_test_greater_than_twenty" +
						"_characters");

		Assertions.assertEquals(950, jsonResponse.getStatus().getCode());
	}

	@Test
	/* NULL password parameter*/
	public void testPost16() {
		Response jsonResponse = addUserToServer(
				"name_test", "surname_test",
				"email_test@gmail.com", null);

		Assertions.assertEquals(950, jsonResponse.getStatus().getCode());
	}



	/* utility post user */
	public Response addUserToServer(String name, String surname, String email, String password){
		Request request = new Request(Method.POST, url);
		User user = new User(name, surname, email, password, null);
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);
		return jsonResponse;
	}

	@Test
	/*  delete EventsRegistry's DB ---> GENERIC_SQL = 951 */
	public void testPost7() throws SQLException {
		Request request = new Request(Method.POST, url);
		User user = new User("name_test", "surname_test", "email_test@gmail.com", "password_test", null);
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);

		DBManager.executeUpdate("drop database events_registry;");
		Response jsonResponse = client.handle(request);

		assertEquals(ErrorCodes.GENERIC_SQL, jsonResponse.getStatus().getCode());
		DBManager.createDB();
	}

	///////////////////////////////////////////PUT////////////////////////////////////////////////////

	@Test
	/* update user -> post a user object, then update it  - only email parameter don't change for update */
	public void testPut1() {
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
	/* adding user with wrong credentials: empty space as email
	* previously captured and for this ErrorCode is 901 */
	public void testPut2() {
		Response jsonResponse = this.updateUserToServer("name_test", "surname_test", "", "password_test");

		Assertions.assertEquals(901, jsonResponse.getStatus().getCode());
	}

	@Test
	/* adding user with wrong credentials: empty space as name*/
	public void testPut3() {
		Response jsonResponse = updateUserToServer("", "surname_test",
				"email_test@gmail.com", "password_test");

		Assertions.assertEquals(950, jsonResponse.getStatus().getCode());
	}

	@Test
	/* adding user with wrong credentials: empty space as surname*/
	public void testPut4() {
		Response jsonResponse = updateUserToServer("name_test", "",
				"email_test@gmail.com", "password_test");

		Assertions.assertEquals(950, jsonResponse.getStatus().getCode());
	}

	@Test
	/* adding user with wrong credentials: empty space as password*/
	public void testPut5() {
		Response jsonResponse = updateUserToServer("name_test", "surname_test",
				"email_test@gmail.com", "");

		Assertions.assertEquals(950, jsonResponse.getStatus().getCode());
	}

	@Test
	/* NULL email parameter
	 * previously captured and for this ErrorCode is 901 */
	public void testPut6() {
		Response jsonResponse = updateUserToServer("name_test", "surname_test",
				null, "password_test");

		Assertions.assertEquals(901, jsonResponse.getStatus().getCode());
	}

	@Test
	/* email length > 80
	 * previously captured and for this ErrorCode is 901 */
	public void testPut7() {
		Response jsonResponse = updateUserToServer("name_test", "surname_test",
				"email_length_is_greater_than_eighty_so_the_test_case_code_is_950" +
						"email_length_is_greater_than_eighty_so_the_test_case_code_is_950" +
						"@gmail.com",
				"password_test");

		Assertions.assertEquals(901, jsonResponse.getStatus().getCode());
	}

	@Test
	/* not email is valid
	 * previously captured and for this ErrorCode is 901 */
	public void testPut8() {
		Response jsonResponse = updateUserToServer("name_test", "surname_test",
				"INVALID_FORMAT_EMAIL", "password_test");

		Assertions.assertEquals(901, jsonResponse.getStatus().getCode());
	}

	@Test
	/* NULL name parameter */
	public void testPut9() {
		Response jsonResponse = updateUserToServer(null, "surname_test",
				"email_test@gmail.com", "password_test");

		Assertions.assertEquals(950, jsonResponse.getStatus().getCode());
	}

	@Test
	/* NULL surname parameter */
	public void testPut10() {
		Response jsonResponse = updateUserToServer("name_test", null,
				"email_test@gmail.com", "password_test");

		Assertions.assertEquals(950, jsonResponse.getStatus().getCode());
	}

	@Test
	/* name length > 80 */
	public void testPut11() {
		Response jsonResponse = updateUserToServer(
				"name_length_greater_than_eighty_name_length_greater_than_eighty_" +
						"name_length_greater_than_eighty_name_length_greater_than_eighty",
				"surname_test",
				"email_test@gmail.com", "password_test");

		Assertions.assertEquals(950, jsonResponse.getStatus().getCode());
	}

	@Test
	/* surname length > 80 */
	public void testPut12() {
		Response jsonResponse = updateUserToServer(
				"name_test", "surname_test_greater_than_eigthy_surname_test_" +
						"greater_than_eigthy_surname_test_greater_than_eigthy_surname_test_" +
						"greater_than_eigthy",
				"email_test@gmail.com", "password_test");

		Assertions.assertEquals(950, jsonResponse.getStatus().getCode());
	}

	@Test
	/* password length > 20 */
	public void testPut13() {
		Response jsonResponse = updateUserToServer(
				"name_test", "surname_test",
				"email_test@gmail.com", "password_test_greater_than_twenty" +
						"_characters");

		Assertions.assertEquals(950, jsonResponse.getStatus().getCode());
	}

	@Test
	/* NULL password parameter*/
	public void testPut14() {
		Response jsonResponse = updateUserToServer(
				"name_test", "surname_test",
				"email_test@gmail.com", null);

		Assertions.assertEquals(950, jsonResponse.getStatus().getCode());
	}

	/* utility post user */
	public Response updateUserToServer(String name, String surname, String email, String password){
		Request request = new Request(Method.POST, url);
		User user = new User("name_test", "surname_test", "email_test@gmail.com", "password_test", null);
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		client.handle(request);

		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC,
				"email_test@gmail.com", "password_test");

		request = new Request(Method.PUT, url);
		request.setChallengeResponse(challengeResponse);
		user = new User(name, surname, email, password);
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);
		return jsonResponse;
	}
		
	@Test
	/* update user -> empty space for email parameter, user param email changed ---> UNAUTHORIZED_USER = 901*/
	public void testPut15() {
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
	public void testPut16() {
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


	@Test
	/*  delete EventsRegistry's DB ---> GENERIC_SQL = 951 */
	public void testPut17() throws SQLException {
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

		DBManager.executeUpdate("drop database events_registry;");
		Response jsonResponse = client.handle(request);
		assertEquals(ErrorCodes.GENERIC_SQL, jsonResponse.getStatus().getCode());
		DBManager.createDB();
	}

}
