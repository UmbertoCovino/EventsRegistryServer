package server.web.resources.json;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import commons.exceptions.ErrorCodes;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;
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
import server.backend.UsersAccessObject;
import server.web.frontend.EventsRegistryWebApplication;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class UserPhotoJSONTest {

	private Client client = new Client(Protocol.HTTP);
	private static String url = "http://localhost:8182/eventsRegistry/users";
	private static String photoName1 = "email_test_1@gmail.com.jpg";
	private static String photoName2 = "email_test_2@gmail.com.jpg";
	private static String photoName3 = "email_test_3@gmail.com.jpg";
	private static String photoPath1;
	private static String photoPath2;
	private static String photoPath3;
	private static String email1 = "email_test_1@gmail.com";
	private static String email2 = "email_test_2@gmail.com";
	private static String email3 = "email_test_3@gmail.com";
	private static File source;

	@BeforeAll
	public static void setUpBeforeAll() throws Exception {
		LaunchServerApp.execute();

		FileUtils.cleanDirectory(new File(EventsRegistryWebApplication.USERS_PHOTOS_DIRECTORY));

		photoPath1 = EventsRegistryWebApplication.USERS_PHOTOS_DIRECTORY + photoName1;
		photoPath2 = EventsRegistryWebApplication.USERS_PHOTOS_DIRECTORY + photoName2;
		photoPath3 = EventsRegistryWebApplication.USERS_PHOTOS_DIRECTORY + photoName3;
		source = new File("email_test@gmail.com.jpg");
		FileUtils.copyFile(source, new File(photoPath1));
//		FileUtils.copyFile(source, new File(photoPath2));
//		FileUtils.copyFile(source, new File(photoPath3));

		DBManager.executeUpdate("delete from users;");

		// add a user for resource with guard
		User user1 = new User("name_test", "surname_test", email1,
				"password_test", photoName1);
		User user2 = new User("name_test", "surname_test", email2,
				"password_test", photoName2);
		User user3 = new User("name_test", "surname_test", email3,
				"password_test", photoName3);

		// invece di questo
//		UsersAccessObject.addUser(user1);
//		UsersAccessObject.addUser(user2);
//		UsersAccessObject.addUser(user3);

		// questo
		String url = "http://localhost:8182/eventsRegistry/users";
		Client client = new Client(Protocol.HTTP);
		Request request = new Request(Method.POST, url);
		request.setEntity(EventsRegistryWebApplication.GSON.toJson(user1, User.class), MediaType.APPLICATION_JSON);
		Response jsonResponse = client.handle(request);
		request.setEntity(EventsRegistryWebApplication.GSON.toJson(user2, User.class), MediaType.APPLICATION_JSON);
		jsonResponse = client.handle(request);
		request.setEntity(EventsRegistryWebApplication.GSON.toJson(user3, User.class), MediaType.APPLICATION_JSON);
		jsonResponse = client.handle(request);
	}

	@AfterAll
	public static void tearDownAfterClass() throws Exception {
//		DBManager.executeUpdate("delete from users;");
//		Files.deleteIfExists(Paths.get(photoPath1));
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
		Request request = new Request(Method.GET, url + "/" + email1 + "/photo");

		Response jsonResponse = client.handle(request);

		assertEquals(200, jsonResponse.getStatus().getCode());
	}

	@Test
	/* foto non presente nel DB ---> 204 - NO CONTENT */
	public void testGet2() throws IOException {
		Request request = new Request(Method.GET, url + "/" + email2 + "/photo");

		Response jsonResponse = client.handle(request);

		assertEquals(204, jsonResponse.getStatus().getCode());
	}

	/////////////////////////////////////////////PUT///////////////////////////////////////////////

	@Test
	/* all parameters ok ---> 200 OK */
	/* problem: error code returned ---> 415 Unsupported Media Type */
	public void put1() throws ResourceException, IOException {
//		Files.deleteIfExists(Paths.get(photoPath3));
		File new_source = new File(email3 +".jpg");
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC,
				email3, "password_test");
		String URI = url  + "/" + email3 + "/photo";
		Request request = new Request(Method.PUT, URI);
		FileRepresentation payload = new FileRepresentation(source,
				MediaType.IMAGE_JPEG);
		request.setEntity(payload);
		request.setChallengeResponse(challengeResponse);
		Response response = client.handle(request);

		Assertions.assertEquals(200, response.getStatus().getCode());
	}

	@Test
	/* set a MediaType different of JPEG ---> 415 Unsupported Media Type */
	public void put2() throws ResourceException, IOException {
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC,
				email1, "password_test");
		String URI = url  + "/" + email1 + "/photo";
		File new_source = new File(email1 + ".jpg");
		Request request = new Request(Method.PUT, URI);
		FileRepresentation payload = new FileRepresentation(source,
				MediaType.IMAGE_GIF);
		request.setEntity(payload);
		request.setChallengeResponse(challengeResponse);
		Response response = client.handle(request);

		Assertions.assertEquals(415, response.getStatus().getCode());
	}

	@Test
	/* user passed with guard -> email_passed != email_user ---> UNAUTHORIZED_USER = 901*/
	public void put0() throws ResourceException, IOException {
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC,
				email1, "password_test");
		String URI = url  + "/" + email3 + "/photo";
		Request request = new Request(Method.PUT, URI);
		FileRepresentation payload = new FileRepresentation(source,
				MediaType.IMAGE_JPEG);
		request.setEntity(payload);
		request.setChallengeResponse(challengeResponse);
		Response response = client.handle(request);

		Assertions.assertEquals(ErrorCodes.UNAUTHORIZED_USER, response.getStatus().getCode());
	}

//	public void deleteFile(String path) throws IOException {
//		/*File file = new File(path);
//		if (file.exists()){
//			return file.delete();
//		} else return true;*/
//	}

	// restanti test vanno fatti su logging e autorizzazione a fare il PUT
}
