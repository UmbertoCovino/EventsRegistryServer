package server.web.resources.json;

import java.text.ParseException;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;
import org.restlet.security.MapVerifier;

import com.google.gson.Gson;

import commons.ErrorCodes;
import commons.InvalidUserEmailException;
import commons.UnauthorizedUserException;
import commons.User;
import server.backend.wrapper.UsersRegistryAPI;

public class UsersRegistryJSON extends ServerResource {

	@Get
	public String getUsers() throws ParseException, InvalidUserEmailException {   	
		Gson gson = new Gson();
		UsersRegistryAPI urapi = UsersRegistryAPI.instance();
		
		String[] emails = urapi.emails();
		User[] users = new User[emails.length];
		
		for (int i = 0; i < emails.length; i++)
			users[i] = urapi.get(emails[i]).cloneWithoutPassword();
		
		return gson.toJson(users, User[].class);
	}
    
    @Post
    public String addUser(String payload) throws ParseException {   	
		Gson gson = new Gson();
		UsersRegistryAPI urapi = UsersRegistryAPI.instance();
		
		User user = gson.fromJson(payload, User.class);
		try {
			user.setPhotoPath(user.getEmail() + ".jpg");
			
			urapi.add(user);
			
			((MapVerifier) getContext().getDefaultVerifier()).getLocalSecrets().put(user.getEmail(), user.getPassword().toCharArray());
			
			return gson.toJson(true, boolean.class);
//			return gson.toJson("User with email " + user.getEmail() + " added.", String.class);
//			return gson.toJson(user.getEmail(), String.class);
//			return gson.toJson(user.cloneWithoutPassword(), User.class);
		} catch (InvalidUserEmailException e) {    		
			Status status = new Status(ErrorCodes.INVALID_USER_EMAIL);
			setStatus(status);
			
			return gson.toJson(e, InvalidUserEmailException.class);
		}    		
    }
    
    @Put
    public String updateUser(String payload) throws ParseException, InvalidUserEmailException {
		Gson gson = new Gson();
		UsersRegistryAPI urapi = UsersRegistryAPI.instance();
		
		User user = gson.fromJson(payload, User.class);
		try {
			if (!getClientInfo().getUser().getIdentifier().equals(user.getEmail()))
				throw new UnauthorizedUserException("You are not authorized.");
			
			urapi.update(user);
			
			((MapVerifier) getContext().getDefaultVerifier()).getLocalSecrets().put(user.getEmail(), user.getPassword().toCharArray());
			
//			return gson.toJson("User with email " + user.getEmail() + " updated.", String.class);
			return gson.toJson(true, boolean.class);
		} catch (UnauthorizedUserException e) {
			Status status = new Status(ErrorCodes.UNAUTHORIZED_USER);
			setStatus(status);
			
			return gson.toJson(e, UnauthorizedUserException.class);
		}
    }
    
//    @Delete
//    public String deleteAll() {
//		//to be implemented
//		
//		return null;
//    }
}
