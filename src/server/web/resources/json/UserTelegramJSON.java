package server.web.resources.json;

import java.text.ParseException;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;

import commons.exceptions.ErrorCodes;
import commons.exceptions.InvalidUserEmailException;
import commons.User;
import server.backend.TelegramUsersRegistry;
import server.backend.wrapper.UsersRegistryAPI;

public class UserTelegramJSON extends ServerResource {

	@Get
    public String getTelegramUser() {
		TelegramUsersRegistry urtokapi = TelegramUsersRegistry.instance();
		Integer token = urtokapi.getNextAvailableToken();
		Gson gson = new Gson();
		UsersRegistryAPI urapi = UsersRegistryAPI.instance();
		
		try {
			User user = urapi.get(getAttribute("email"));
			urtokapi.add(user, token);
			
			return gson.toJson(token, Integer.class); 
		} catch (InvalidUserEmailException e) {
			Status status = new Status(ErrorCodes.INVALID_USER_EMAIL);
			setStatus(status);
			
			return gson.toJson(e, InvalidUserEmailException.class);
		}
	}
	
}