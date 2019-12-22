package server.web.resources.json;

import java.text.ParseException;
import java.util.ArrayList;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;
import org.restlet.security.MapVerifier;

import com.google.gson.Gson;

import commons.ErrorCodes;
import commons.GenericSQLException;
import commons.InvalidUserEmailException;
import commons.UnauthorizedUserException;
import commons.User;
import server.backend.UsersAccessObject;
import server.web.frontend.EventsRegistryWebApplication;

public class UsersJSON extends ServerResource {

	@Get
	public String getUsers() throws ParseException, InvalidUserEmailException {   	
		Gson gson = EventsRegistryWebApplication.GSON;
		
		ArrayList<User> users = null;
		try {
			users = UsersAccessObject.getUsers();
		} catch (GenericSQLException e) {
			Status status = new Status(ErrorCodes.GENERIC_SQL);
			setStatus(status);
			
			return gson.toJson(e, GenericSQLException.class);
		}
		
		return gson.toJson(users.toArray(new User[users.size()]), User[].class);
	}
    
    @Post
    public String addUser(String payload) throws ParseException {   	
    		Gson gson = EventsRegistryWebApplication.GSON;
		
		User user = gson.fromJson(payload, User.class);
		try {
			UsersAccessObject.addUser(user);
			
			String email = user.getEmail();
			String photoPath = email + ".jpg";
			
			UsersAccessObject.updateUserPhotoPath(email, photoPath);
			
			((MapVerifier) getContext().getDefaultVerifier()).getLocalSecrets().put(user.getEmail(), user.getPassword().toCharArray());
			
			return gson.toJson(true, boolean.class);
//			return gson.toJson("User with email " + user.getEmail() + " added.", String.class);
//			return gson.toJson(user.getEmail(), String.class);
//			return gson.toJson(user.cloneWithoutPassword(), User.class);
		} catch (InvalidUserEmailException e) {    		
			Status status = new Status(ErrorCodes.INVALID_USER_EMAIL);
			setStatus(status);
			
			return gson.toJson(e, InvalidUserEmailException.class);
		} catch (GenericSQLException e) {
			Status status = new Status(ErrorCodes.GENERIC_SQL);
			setStatus(status);
			
			return gson.toJson(e, GenericSQLException.class);
		}		
    }
    
    @Put
    public String updateUser(String payload) throws ParseException, InvalidUserEmailException {
    		Gson gson = EventsRegistryWebApplication.GSON;
		
		User user = gson.fromJson(payload, User.class);
		try {
			if (!getClientInfo().getUser().getIdentifier().equals(user.getEmail()))
				throw new UnauthorizedUserException("You are not authorized.");
			
			UsersAccessObject.updateUser(user);
			
			((MapVerifier) getContext().getDefaultVerifier()).getLocalSecrets().put(user.getEmail(), user.getPassword().toCharArray());
			
//			return gson.toJson("User with email " + user.getEmail() + " updated.", String.class);
			return gson.toJson(true, boolean.class);
		} catch (UnauthorizedUserException e) {
			Status status = new Status(ErrorCodes.UNAUTHORIZED_USER);
			setStatus(status);
			
			return gson.toJson(e, UnauthorizedUserException.class);
		} catch (GenericSQLException e) {
			Status status = new Status(ErrorCodes.GENERIC_SQL);
			setStatus(status);
			
			return gson.toJson(e, GenericSQLException.class);
		}	
    }
    
//    @Delete
//    public String deleteAll() {
//		//to be implemented
//		
//		return null;
//    }
}
