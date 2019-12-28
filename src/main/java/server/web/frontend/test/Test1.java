package server.web.frontend.test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import javax.swing.JFileChooser;

import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import com.google.gson.Gson;

import commons.Event;
import commons.User;
import commons.exceptions.ErrorCodes;
import commons.exceptions.InvalidEventIdException;
import commons.exceptions.InvalidUserEmailException;


public class Test1 {
	private final static String baseURI = "http://localhost:8182/eventsRegistry/";

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {
//		System.out.println(testPostUserType("users", new User("Umberto", "Covino", "upcovino@gmail.com", "1234"), User.class, null, null));
//		System.out.println();
		
		
		
//		System.out.println(testPostEventType("events", new Event("Concerto Vasco Rossi", new Date(118, 1, 5), new Date(118, 1, 5, 21, 00), new Date(118, 1, 5, 00, 00), 
//				"Vasco sar� qui."), Event.class, "upcovino@gmail.com", "1234"));
//		System.out.println();
		
		
		
//		System.out.println(testGetEventType("events/dca4dc93-1bc7-4a85-b161-83df7a19de2e/photo", String.class, null, null));
//		System.out.println();
		
		System.out.println(Arrays.toString(testGetEventType("events/05.02.2018/09.02.2018", Event[].class, null, null)));
		System.out.println();
		
//		JFileChooser fc = new JFileChooser();
//		
//		int returnVal = fc.showOpenDialog(null);
//		if (returnVal == JFileChooser.APPROVE_OPTION)
//			testPutPhotoEventType("events/dca4dc93-1bc7-4a85-b161-83df7a19de2e/photo", fc.getSelectedFile(), "upcovino@gmail.com", "1234");
//		else 	
//			System.out.println("No file selected.");	
		
		
		
//		System.out.println(testPostEventType("events", new Event("Uniti per il Benevento", new Date(118, 1, 7), new Date(118, 1, 7, 11, 00), new Date(118, 1, 7, 20, 00), 
//				"Forza Benevento!"), Event.class, "upcovino@gmail.com", "1234"));
//		System.out.println();
		
		
		
		System.out.println(testGetUserType("users/upcovino@gmail.com", User.class, null, null));
		System.out.println();
		
		
		
		System.out.println(testGetEventType("events/dca4dc93-1bc7-4a85-b161-83df7a19de2e", Event.class, null, null));
		System.out.println();
		
		
		
		System.out.println(Arrays.toString(testGetEventType("events", Event[].class, null, null)));
	}
	
	
	
	
	
	private static <T, E extends Throwable> T testGet(String uri, Class<T> returnResourceClass, int errorCode, Class<E> exceptionClass, String email, String password) throws E {
		ClientResource cr;
		Gson gson = new Gson();
		
		String URI = baseURI + uri;
		String jsonResponse = null;

		System.out.println("Testing get on URI: " + URI);
		cr = new ClientResource(URI);
		
		if (email != null)
			cr.setChallengeResponse(ChallengeScheme.HTTP_BASIC, email, password);
		
		try {
			jsonResponse = cr.get().getText();
			
			if (cr.getStatus().getCode() == errorCode)
				throw gson.fromJson(jsonResponse, exceptionClass);
			
			System.out.print("Response: ");
			return gson.fromJson(jsonResponse, returnResourceClass);
		} catch (ResourceException | IOException e1) {
			System.out.println("Error: " + cr.getStatus().getCode() + " - " + cr.getStatus().getDescription()+ " - " + cr.getStatus().getReasonPhrase());
		} catch (Exception e2) {
			if (exceptionClass.isInstance(e2))
				System.out.println("Error: " + cr.getStatus().getCode() + " - " + e2.getMessage());		
		}
		return null;
	}
	
	private static <T, E extends Throwable> String testPost(String uri, T resource, Class<T> resourceClass, int errorCode, Class<E> exceptionClass, String email, String password) throws E {
		ClientResource cr;
		Gson gson = new Gson();
		
		String URI = baseURI + uri;		
		String jsonResponse = null;
		
		System.out.println("Testing post on URI: " + URI);
		cr = new ClientResource(URI);
		
		if (email != null)
			cr.setChallengeResponse(ChallengeScheme.HTTP_BASIC, email, password);

		try {
			jsonResponse = cr.post(gson.toJson(resource, resourceClass)).getText();
			
			if (cr.getStatus().getCode() == errorCode)
				throw gson.fromJson(jsonResponse, exceptionClass);
			
			System.out.print("Response: ");
			return gson.fromJson(jsonResponse, String.class);
		} catch (ResourceException | IOException e1) {
			System.out.println("Error: " + cr.getStatus().getCode() + " - " + cr.getStatus().getDescription()+ " - " + cr.getStatus().getReasonPhrase());
		} catch (Exception e2) {
			if (exceptionClass.isInstance(e2))
				System.out.println("Error: " + cr.getStatus().getCode() + " - " + e2.getMessage());		
		}
		return null;
	}
	
	private static <T> T testGetEventType(String uri, Class<T> returnResourceClass, String email, String password) throws InvalidEventIdException {
		return testGet(uri, returnResourceClass, ErrorCodes.INVALID_EVENT_ID, InvalidEventIdException.class, email, password);
	}
	
	private static <T> String testPostEventType(String uri, T resource, Class<T> resourceClass, String email, String password) throws InvalidEventIdException {
		return testPost(uri, resource, resourceClass, ErrorCodes.INVALID_EVENT_ID, InvalidEventIdException.class, email, password);
	}
	
	private static <T> T testGetUserType(String uri, Class<T> returnResourceClass, String email, String password) throws InvalidUserEmailException {
		return testGet(uri, returnResourceClass, ErrorCodes.INVALID_USER_EMAIL, InvalidUserEmailException.class, email, password);
	}
	
	private static <T> String testPostUserType(String uri, T resource, Class<T> resourceClass, String email, String password) throws InvalidUserEmailException {
		return testPost(uri, resource, resourceClass, ErrorCodes.INVALID_USER_EMAIL, InvalidUserEmailException.class, email, password);
	}
	
	private static <E extends Throwable> String testPutPhoto(String uri, File file, int errorCode, Class<E> exceptionClass, String email, String password) throws E {
		ClientResource cr;
		Gson gson = new Gson();
		
		String URI = baseURI + uri;		
		String jsonResponse = null;
		
		System.out.println("Testing post on URI: " + URI);
		cr = new ClientResource(URI);
		
		if (email != null)
			cr.setChallengeResponse(ChallengeScheme.HTTP_BASIC, email, password);
		
		FileRepresentation payload = new FileRepresentation(file, MediaType.IMAGE_JPEG);
		try {
			jsonResponse = cr.put(payload).getText();
			
			if (cr.getStatus().getCode() == errorCode)
				throw gson.fromJson(jsonResponse, exceptionClass);
			
			System.out.print("Response: ");
			return gson.fromJson(jsonResponse, String.class);
		} catch (ResourceException | IOException e1) {
			System.out.println("Error: " + cr.getStatus().getCode() + " - " + cr.getStatus().getDescription()+ " - " + cr.getStatus().getReasonPhrase());
		} catch (Exception e2) {
			if (exceptionClass.isInstance(e2))
				System.out.println("Error: " + cr.getStatus().getCode() + " - " + e2.getMessage());		
		}
		return null;
	}
	
	private static String testPutPhotoEventType(String uri, File file, String email, String password) throws InvalidEventIdException {
		return testPutPhoto(uri, file, ErrorCodes.INVALID_EVENT_ID, InvalidEventIdException.class, email, password);
	}
	
	private static String testPutPhotoUserType(String uri, File file, String email, String password) throws InvalidUserEmailException {
		return testPutPhoto(uri, file, ErrorCodes.INVALID_USER_EMAIL, InvalidUserEmailException.class, email, password);
	}
}
