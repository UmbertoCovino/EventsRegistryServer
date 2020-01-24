package server.web.resources.json;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
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
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import com.google.gson.Gson;

import commons.User;
import server.backend.DBManager;
import server.web.frontend.EventsRegistryWebApplication;

class UserPhotoJSONTest {
	
	private static Gson gson;
	private Client client = new Client(Protocol.HTTP);
	private static String url = "http://localhost:8182/eventsRegistry/users";
	private static String photoPath = "email_test@gmail.com.jpg";
	private static String email = "email_test@gmail.com";

	@BeforeAll
	public static void setUpBeforeAll() throws Exception {				
		LaunchServerApp.execute();
		gson = EventsRegistryWebApplication.GSON;

		DBManager.executeUpdate("delete from users;");
				
		// add a user for resource with guard		
		Client client = new Client(Protocol.HTTP);
		Request request = new Request(Method.POST, url);
		User user = new User("name_test", "surname_test", "email_test@gmail.com", "password_test", null);
		request.setEntity(gson.toJson(user, User.class), MediaType.APPLICATION_JSON);
		client.handle(request);
	}
	
	@AfterAll
	public static void tearDownAfterClass() throws Exception {
		DBManager.executeUpdate("delete from users;");
		
		File photo_file = new File(EventsRegistryWebApplication.USERS_PHOTOS_DIRECTORY + photoPath);
		photo_file.delete();
	}
	
	@BeforeEach
	public void setUp() throws Exception {
		File source = new File(photoPath);
		File dest = new File(EventsRegistryWebApplication.USERS_PHOTOS_DIRECTORY + photoPath);
		try {
		    FileUtils.copyFile(source, dest);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	
	@AfterEach
	public void tearDown() throws Exception {
		
	}
	
	/////////////////////////////////////////////GET///////////////////////////////////////////////
	
	@Test
	/* foto presente nel DB */
	public void testGet1() {
		String email = "email_test@gmail.com";
		Request request = new Request(Method.GET, url + "/" + email + "/photo");
		
		Response jsonResponse = client.handle(request);

		assertEquals(200, jsonResponse.getStatus().getCode());
	}
	
	@Test
	/* foto non presente nel DB ---> 204 - NO CONTENT */
	public void testGet2() {
		File photo_file = new File(EventsRegistryWebApplication.USERS_PHOTOS_DIRECTORY + photoPath);
		photo_file.delete();
		
		String email = "email_test@gmail.com";
		Request request = new Request(Method.GET, url + "/" + email + "/photo");
		
		Response jsonResponse = client.handle(request);
		
		assertEquals(204, jsonResponse.getStatus().getCode());
	}
	
	/////////////////////////////////////////////PUT///////////////////////////////////////////////
	
	@Test 
	/* all parameters ok ---> 200 OK */
	/* problem: error code returned ---> 415 Unsupported Media Type */
	public void put1() throws ResourceException, IOException {
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, "email_test@gmail.com", 
				"password_test");
		Request request = new Request(Method.PUT, url);
		FileRepresentation payload = new FileRepresentation(new File(photoPath),
                 MediaType.IMAGE_JPEG);
		request.setEntity(payload);
		request.setChallengeResponse(challengeResponse);
		Response response = client.handle(request);
        
		Assertions.assertEquals(200, response.getStatus());
	}
	
	@Test 
	/* set a MediaType different of JPEG ---> 415 Unsupported Media Type */
	public void put2() throws ResourceException, IOException {
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, "email_test@gmail.com", 
				"password_test");
		Request request = new Request(Method.PUT, url);
		FileRepresentation payload = new FileRepresentation(new File(photoPath),
                 MediaType.IMAGE_PNG);
		request.setEntity(payload);
		request.setChallengeResponse(challengeResponse);
		Response response = client.handle(request);
        
		Assertions.assertEquals(1001, response.getStatus().getCode());
	}
	
	
	// restanti test vanno fatti su logging e autorizzazione a fare il PUT
}
