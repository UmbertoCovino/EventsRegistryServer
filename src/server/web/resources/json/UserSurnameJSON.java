package server.web.resources.json;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;

import commons.ErrorCodes;
import commons.GenericSQLException;
import commons.InvalidUserEmailException;
import commons.UnauthorizedUserException;
import server.backend.UsersAccessObject;

public class UserSurnameJSON extends ServerResource {

	@Get
    public String getSurname() {
		Gson gson = new Gson();
		
		try {
			String surname = UsersAccessObject.getUserSurname(getAttribute("email"));
		
			return gson.toJson(surname, String.class);   	
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
    public String updateSurname(String payload) {
		Gson gson = new Gson();
		String email = getAttribute("email");
		
		try {
			if (!getClientInfo().getUser().getIdentifier().equals(getAttribute("email")))
				throw new UnauthorizedUserException("You are not authorized.");
			
			String surname = gson.fromJson(payload, String.class);
			
			UsersAccessObject.updateUserSurname(email, surname);
			
			return gson.toJson("Surname updated for user with email " + email + ".", String.class);
		} catch (InvalidUserEmailException e) {
			Status status = new Status(ErrorCodes.INVALID_USER_EMAIL);
			setStatus(status);
			
			return gson.toJson(e, InvalidUserEmailException.class);
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
}
