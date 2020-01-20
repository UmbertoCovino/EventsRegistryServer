package server.web.resources.json;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;

import commons.exceptions.ErrorCodes;
import commons.exceptions.GenericSQLException;
import commons.exceptions.InvalidUserEmailException;
import commons.exceptions.InvalidUserTokenException;
import commons.exceptions.UnauthorizedUserException;
import commons.exceptions.VoidClassFieldException;
import commons.User;
import server.backend.TelegramUsersAccessObject;
import server.backend.UsersAccessObject;

public class UserTelegramJSON extends ServerResource {

	@Get
    public String getTelegramUser() {	
		Gson gson = new Gson();
		String email = getAttribute("email");
		
		try {
			User user = UsersAccessObject.getUser(email);

			if (!getClientInfo().getUser().getIdentifier().equals(email))
				throw new UnauthorizedUserException("You are not authorized.");
						
			TelegramUsersAccessObject telegram_registry = TelegramUsersAccessObject.instance();
			Integer token = telegram_registry.getNextAvailableToken();
			
			telegram_registry.addUserByToken(token, user);
			
			return gson.toJson(token, Integer.class); 
		} catch (UnauthorizedUserException e) {
			Status status = new Status(ErrorCodes.UNAUTHORIZED_USER);
			setStatus(status);
			
			return gson.toJson(e, UnauthorizedUserException.class);
		} catch (InvalidUserEmailException e) {
			Status status = new Status(ErrorCodes.INVALID_USER_EMAIL);
			setStatus(status);
			
			return gson.toJson(e, InvalidUserEmailException.class);
		} catch (VoidClassFieldException e) {
			Status status = new Status(ErrorCodes.VOID_CLASS_FIELD);
			setStatus(status);
			
			return gson.toJson(e, VoidClassFieldException.class);
		} catch (GenericSQLException e) {
			Status status = new Status(ErrorCodes.GENERIC_SQL);
			setStatus(status);
			
			return gson.toJson(e, GenericSQLException.class);
		} catch (InvalidUserTokenException e) {
			Status status = new Status(ErrorCodes.INVALID_TOKEN);
			setStatus(status);
			
			return gson.toJson(e, InvalidUserTokenException.class);
		}
	}
	
}