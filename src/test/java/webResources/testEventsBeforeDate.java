package webResources;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.data.Protocol;

import server.backend.DBManager;
import server.web.frontend.EventsRegistryWebApplication;

class testEventsBeforeDate {

	private String url = "http://localhost:8182/eventsRegistry/events";
	private Client client = new Client(Protocol.HTTP);
	
	@BeforeAll
	public static void setUpBeforeAll() throws Exception {				
		EventsRegistryWebApplication.main(null);
	}
	
	@AfterAll
	public static void tearDownAfterClass() throws Exception {
	}
	
	@BeforeEach
	public void setUp() throws Exception {
		DBManager.executeUpdate("delete from events;");
	}
	
	@AfterEach
	public void tearDown() throws Exception {
		DBManager.executeUpdate("delete from events;");
	}
	
	@Test
	/* all parameter are valid ---> 200 OK */
	public void testGet1() {
		// dovrei aggiungere un evento dopo e verificare che il GET abbia restituito quello
		Request request = new Request(Method.GET, url+"/before/"+"2020-01-10 10:00:00");
		Response jsonResponse = client.handle(request);

		assertEquals(200, jsonResponse.getStatus().getCode());
	}
	
	@Test
	/* invalid format for date attribute ---> DATE_PARSING = 953 */
	public void testGet2() {
		// dovrei aggiungere un evento dopo e verificare che il GET abbia restituito quello
		Request request = new Request(Method.GET, url+"/before/"+"2020.01.10 10:00:00");
		Response jsonResponse = client.handle(request);

		assertEquals(953, jsonResponse.getStatus().getCode());
	}

}
