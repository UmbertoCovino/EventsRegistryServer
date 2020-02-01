package server.web.resources.json;

import com.google.gson.Gson;
import commons.Event;
import commons.User;
import commons.exceptions.GenericSQLException;
import commons.exceptions.InvalidEventIdException;
import commons.exceptions.VoidClassFieldException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.*;
import server.backend.DBManager;
import server.backend.EventsAccessObject;
import server.web.frontend.EventsRegistryWebApplication;

import java.text.ParseException;

public class TelegramBotNotificationTest {
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

    @Test
    public void testTelegramNotification(){
        Request request = new Request(Method.POST, url + "/" + eventId + "/subscribers");
        ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC,
                "email_test@gmail.com", "password_test");
        String email = "email_test@gmail.com";
        request.setChallengeResponse(challengeResponse);
        request.setEntity(gson.toJson(email, String.class), MediaType.APPLICATION_JSON);
        Response jsonResponse = client.handle(request);

    }

}
