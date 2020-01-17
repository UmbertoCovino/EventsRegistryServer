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
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;

import com.google.gson.Gson;

import commons.User;
import server.backend.DBManager;
import server.web.frontend.EventsRegistryWebApplication;

class UserJSONTest {

	private static Gson gson = new Gson();
	private static String url = "http://localhost:8182/eventsRegistry/users/";
	
	@BeforeAll
	public static void setUpBeforeAll() throws Exception {				
		EventsRegistryWebApplication.main(null);
	}
	
	@AfterAll
	public static void tearDownAfterClass() throws Exception {
	}
	
	@BeforeEach
	public void setUp() throws Exception {
		DBManager.executeUpdate("delete from users;");
	}
	
	@AfterEach
	public void tearDown() throws Exception {
		DBManager.executeUpdate("delete from users;");
	}
	
	/////////////////////////////////////////GET////////////////////////////////////////////////////////////
	
	@Test
	/* non manca una guard? E' possibile ottenere un oggetto User solo grazie alla email? 
	 * Sì perché mi restituisce un oggetto User senza password */
	public void testGet1() {
		String url_users = "http://localhost:8182/eventsRegistry/users";
		Client client = new Client(Protocol.HTTP);
		Request request = new Request(Method.POST, url_users);
		User user = new User("name_test", "surname_test", "email_test", "password_test", "email_test.jpg");
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		client.handle(request);
		
		request = new Request(Method.GET, url + user.getEmail());
		Response jsonResponse =  client.handle(request);
		
		user = new User("name_test", "surname_test", "email_test", "email_test.jpg");
		User got_user = gson.fromJson(jsonResponse.getEntityAsText(), User.class);
		
		assertEquals(user.toString(), got_user.toString()); 
		//metodi alternativi per verificare l'equivalenza tra due oggetti User direttamente???
		//*non usando quindi il toString()
	}
	
	@Test
	/* wrong parameter: invalid attribute email ---> INVALID_USER_EMAIL = 900 */
	public void testGet2() {
		String url_users = "http://localhost:8182/eventsRegistry/users";
		Client client = new Client(Protocol.HTTP);
		Request request = new Request(Method.POST, url_users);
		User user = new User("name_test", "surname_test", "email_test", "password_test", "email_test.jpg");
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		client.handle(request);
		
		request = new Request(Method.GET, url + "email_test_INVALID");
		Response jsonResponse =  client.handle(request);
		
		assertEquals(900, jsonResponse.getStatus().getCode());
	}
	
	///////////////////////////////////////////POST//////////////////////////////////////////////////////
	
	// non mi è chiara la funzione del POST nella resource class UserJson
	
	//////////////////////////////////////////DELETE////////////////////////////////////////////////////
	
	// (?) ma non ci vuole una guard per eliminare un utente? test dopo aver verificato questa cosa.
	
	
}
