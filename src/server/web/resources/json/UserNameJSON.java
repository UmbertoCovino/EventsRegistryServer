package server.web.resources.json;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;

import commons.ErrorCodes;
import commons.InvalidUserEmailException;
import commons.UnauthorizedUserException;
import commons.User;
import server.backend.wrapper.UsersRegistryAPI;

public class UserNameJSON extends ServerResource {

	@Get
    public String getName() {
		Gson gson = new Gson();
		UsersRegistryAPI urapi = UsersRegistryAPI.instance();
		
		try {
			User user = urapi.get(getAttribute("email"));
		
			return gson.toJson(user.getName(), String.class);   	
		} catch (InvalidUserEmailException e) {
			Status status = new Status(ErrorCodes.INVALID_USER_EMAIL);
			setStatus(status);
			
			return gson.toJson(e, InvalidUserEmailException.class);
		}
    }
    
    @Put
    public String updateName(String payload) {
		Gson gson = new Gson();
		UsersRegistryAPI urapi = UsersRegistryAPI.instance();
		
		try {
			if (!getClientInfo().getUser().getIdentifier().equals(getAttribute("email")))
				throw new UnauthorizedUserException("You are not authorized.");
			
			User user = urapi.get(getAttribute("email"));
			user.setName(gson.fromJson(payload, String.class));
			urapi.update(user);
			
			return gson.toJson("Name updated for user with email " + getAttribute("email") + ".", String.class);
		} catch (InvalidUserEmailException e) {
			Status status = new Status(ErrorCodes.INVALID_USER_EMAIL);
			setStatus(status);
			
			return gson.toJson(e, InvalidUserEmailException.class);
		} catch (UnauthorizedUserException e) {
			Status status = new Status(ErrorCodes.UNAUTHORIZED_USER);
			setStatus(status);
			
			return gson.toJson(e, UnauthorizedUserException.class);
		}
	}
}
