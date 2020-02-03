package server.web.resources.json;

import com.google.gson.Gson;
import org.junit.jupiter.api.*;

import org.restlet.Client;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.*;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import server.backend.DBManager;
import server.backend.TelegramBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import server.web.frontend.EventsRegistryWebApplication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TelegramBotTest {

    private static Gson gson;
    private static Client client;
    private static String url = "http://localhost:8182/eventsRegistry/users";
    private static TelegramBot bot;
    private static int token;

    @BeforeAll
    public static void setUpBeforeAll() throws Exception {
        LaunchServerApp.execute();
        gson = EventsRegistryWebApplication.GSON;
        client = new Client(Protocol.HTTP);
        bot = new TelegramBot();

        DBManager.executeUpdate("drop database events_registry;");
        DBManager.createDB();
        // add a user for resource with guard
        Client client = new Client(Protocol.HTTP);
        Request request = new Request(Method.POST, url);
        commons.User user1 = new commons.User("name_test_1", "surname_test_1", "email_test_1@gmail.com", "password_test_1", null);
        request.setEntity(gson.toJson(user1, commons.User.class), MediaType.APPLICATION_JSON);
        client.handle(request);
        String email = "email_test_1@gmail.com";
        request = new Request(Method.GET, url + "/" + email + "/telegram");
        ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC,
                "email_test_1@gmail.com", "password_test_1");

        request.setChallengeResponse(challengeResponse);
        Response jsonResponse = client.handle(request);
        System.out.println(jsonResponse.getEntityAsText());
        token = gson.fromJson(jsonResponse.getEntityAsText(), Integer.class);
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

    @Test
    public void testOnUpdateReceived1(){
        int chat_id = 190132843;
        String text = "/start="+token;
        User endUser = new User(chat_id, "Angelo", false, "",
                "angelocaporaso", "");
        Update update = mockFullUpdate(endUser, text);
        Message response = bot.onUpdateReceivedCaller(update);
        assertEquals(chat_id + " - " + text, response.getText());
    }

    @Test
    public void testOnUpdateReceived2(){
        //token not valid --> non verrà trovato l'utente, poiché nessun utente è associato a questo token
        int token = 100;
        int chat_id = 190132843;
        String text = "/start="+token;
        User endUser = new User(chat_id, "Angelo", false, "",
                "angelocaporaso", "");
        Update update = mockFullUpdate(endUser, text);
        Exception exception = assertThrows(NullPointerException.class, () ->
                bot.onUpdateReceivedCaller(update));
    }

    @Test
    public void testOnUpdateReceived3(){
        int chat_id = 190132844;
        String text = "/start="+token;
        User endUser = new User(chat_id, "Angelo", false, "",
                "angelocaporaso", "");
        Update update = mockFullUpdate(endUser, text);
        Message response = bot.onUpdateReceivedCaller(update);
        // catched -> org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException:
        // Error sending message: [400] Bad Request: chat not found
        assertEquals(null, response);
    }

    @Test
    public void testOnUpdateReceived4(){
        int chat_id = 190132843;
        String text = "/start="+token;
        User endUser = new User(chat_id, "Angelo", false, "",
                "angelocaporaso", "");
        // update without Message
        Update update = mock(Update.class);
        Message response = bot.onUpdateReceivedCaller(update);
        // check if(update.hasMessage())
        assertEquals(null, response);
    }

    @Test
    public void testOnUpdateReceived5(){
        int chat_id = 190132843;
        String text = "/start="+token;
        User endUser = new User(chat_id, "Angelo", false, "",
                "angelocaporaso", "");
        // update without Message TEXT
        Update update = mockFullUpdateWithoutText(endUser, text);
        // check if(update.getMessage().hasText())
        Exception exception = assertThrows(NullPointerException.class, () ->
                bot.onUpdateReceivedCaller(update));
    }

    @Test
    public void testOnUpdateReceived6(){
        int chat_id = 190132843;
        // message doesn't respect start pattern
        String text = "/stop="+token;
        String mock_text = "/start="+token;
        User endUser = new User(chat_id, "Angelo", false, "",
                "angelocaporaso", "");
        Update update = mockFullUpdate(endUser, text);
        Message response = bot.onUpdateReceivedCaller(update);
        assertNotEquals(chat_id + " - " + mock_text, response.getText());
    }

    static Update mockFullUpdateWithoutText(User user, String args) {
        Update update = mock(Update.class);
        when(update.hasMessage()).thenReturn(true);
        Message message = mock(Message.class);
        when(message.getFrom()).thenReturn(user);
        when(message.hasText()).thenReturn(true);
        when(message.isUserMessage()).thenReturn(true);
        when(message.getChatId()).thenReturn((long) user.getId());
        when(update.getMessage()).thenReturn(message);
        return update;
    }

   static Update mockFullUpdate(User user, String args) {
        Update update = mock(Update.class);
        when(update.hasMessage()).thenReturn(true);
        Message message = mock(Message.class);
        when(message.getFrom()).thenReturn(user);
        when(message.getText()).thenReturn(args);
        when(message.hasText()).thenReturn(true);
        when(message.isUserMessage()).thenReturn(true);
        when(message.getChatId()).thenReturn((long) user.getId());
        when(update.getMessage()).thenReturn(message);
        return update;
    }
}
