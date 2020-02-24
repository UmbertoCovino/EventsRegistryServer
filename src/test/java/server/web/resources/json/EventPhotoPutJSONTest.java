package server.web.resources.json;

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
import server.web.frontend.EventsRegistryWebApplication;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

class EventPhotoPutJSONTest {

    private static String url = "http://localhost:8182/eventsRegistry/events";
    private static Gson gson;
    private static Client client;
    private static int eventId_1;
    private static File source = new File("email_test@gmail.com.jpg");
    private static String email = "email_test@gmail.com";
    private static String email2 = "email2_test@gmail.com";
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
        user = new User("name_test", "surname_test", email2, password, "email_test.jpg");
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
        eventId_1 = gson.fromJson(jsonResponse.getEntityAsText(), int.class);
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

    /////////////////////////////////////////////PUT///////////////////////////////////////////////

	@Test
	/* all parameters ok ---> 200 OK */
	public void put1() throws ResourceException, IOException {
//		Files.deleteIfExists(Paths.get(photoPath3));
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC,
				email, password);
		String URI = url  + "/" + eventId_1 + "/photo";
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
                email, password);
        String URI = url  + "/" + eventId_1 + "/photo";
        Request request = new Request(Method.PUT, URI);
        FileRepresentation payload = new FileRepresentation(source,
                MediaType.IMAGE_GIF);
        request.setEntity(payload);
        request.setChallengeResponse(challengeResponse);
        Response response = client.handle(request);

        Assertions.assertEquals(415, response.getStatus().getCode());
    }

    @Test
    /* user passed with guard -> emailUserPassed != ownerEventEmail ---> UNAUTHORIZED_USER = 901*/
    public void put3() throws ResourceException, IOException {
        ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC,
                email2, password);
        String URI = url  + "/" + eventId_1 + "/photo";
        Request request = new Request(Method.PUT, URI);
        FileRepresentation payload = new FileRepresentation(source,
                MediaType.IMAGE_GIF);
        request.setEntity(payload);
        request.setChallengeResponse(challengeResponse);
        Response response = client.handle(request);

        Assertions.assertEquals(415, response.getStatus().getCode());
    }

}
