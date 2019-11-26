package server.web.resources.json;

import java.io.File;
import java.text.ParseException;

import org.restlet.data.Status;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.restlet.security.MapVerifier;

import com.google.gson.Gson;

import commons.ErrorCodes;
import commons.InvalidUserEmailException;
import commons.User;
import commons.UnauthorizedUserException;
import server.backend.wrapper.UsersRegistryAPI;

public class UserJSON extends ServerResource {

	@Get
    public String getUser() throws ParseException {
		Gson gson = new Gson();
		UsersRegistryAPI urapi = UsersRegistryAPI.instance();
		
		try {
			User user = urapi.get(getAttribute("email"));
		
			return gson.toJson(user.cloneWithoutPassword(), User.class);   	
		} catch (InvalidUserEmailException e) {
			Status status = new Status(ErrorCodes.INVALID_USER_EMAIL);
			setStatus(status);
			
			return gson.toJson(e, InvalidUserEmailException.class);
		}
    }
	
	@Post
    public String getUserIfPasswordIsCorrect(String payload) throws ParseException {
		Gson gson = new Gson();
		UsersRegistryAPI urapi = UsersRegistryAPI.instance();

		String password = gson.fromJson(payload, String.class);
		try {
			User user = urapi.get(getAttribute("email"));
			
			if (password.equals(user.getPassword())) // valutare l'opzione di restituire un booleano
				return gson.toJson(user.cloneWithoutPassword(), User.class);
			else
				return gson.toJson(null, User.class);
		} catch (InvalidUserEmailException e) {
			Status status = new Status(ErrorCodes.INVALID_USER_EMAIL);
			setStatus(status);
			
			return gson.toJson(e, InvalidUserEmailException.class);
		}
    }
    
//    @Delete
//    public String deleteUser() {
//		Gson gson = new Gson();
//		UsersRegistryAPI urapi = UsersRegistryAPI.instance();
//		
//		try {
//			if (!getClientInfo().getUser().getIdentifier().equals(getAttribute("email")))
//				throw new UnauthorizedUserException("You are not authorized.");
//			
//			User user = urapi.get(getAttribute("email"));
//
//			new File(urapi.getPhotosDirectory() + "/" + user.getPhoto()).delete();
//			
//			urapi.remove(getAttribute("email"));
//			
//			((MapVerifier) getContext().getDefaultVerifier()).getLocalSecrets().remove(user.getEmail());
//			
//			return gson.toJson("User with email " + getAttribute("email") + " removed.", String.class);
//		} catch (InvalidUserEmailException e) {
//			Status status = new Status(ErrorCodes.INVALID_USER_EMAIL);
//			setStatus(status);
//			
//			return gson.toJson(e, InvalidUserEmailException.class);
//		} catch (UnauthorizedUserException e) {
//			Status status = new Status(ErrorCodes.UNAUTHORIZED_USER);
//			setStatus(status);
//			
//			return gson.toJson(e, UnauthorizedUserException.class);
//		}
//	}
}
