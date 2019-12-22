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

public class UserPasswordJSON extends ServerResource {

	@Get
    public String getPassword() {
		Gson gson = new Gson();
		String email = getAttribute("email");
		
		try {
			if (!getClientInfo().getUser().getIdentifier().equals(email))
				throw new UnauthorizedUserException("You are not authorized.");
			
			String password = UsersAccessObject.getUserPassword(email);
		
			return gson.toJson(password, String.class);   	
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
    
    @Put
    public String updatePassword(String payload) {
		Gson gson = new Gson();
		String email = getAttribute("email");
		
		try {
			if (!getClientInfo().getUser().getIdentifier().equals(email))
				throw new UnauthorizedUserException("You are not authorized.");
			
			String password = gson.fromJson(payload, String.class);
			
			UsersAccessObject.updateUserPassword(email, password);
			
			return gson.toJson("Password updated for user with email " + email + ".", String.class);
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
