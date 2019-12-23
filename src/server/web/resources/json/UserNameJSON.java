package server.web.resources.json;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import commons.exceptions.ErrorCodes;
import commons.exceptions.GenericSQLException;
import commons.exceptions.InvalidUserEmailException;
import commons.exceptions.JsonParsingException;
import commons.exceptions.UnauthorizedUserException;
import commons.exceptions.VoidClassFieldException;
import server.backend.UsersAccessObject;
import server.web.frontend.EventsRegistryWebApplication;

public class UserNameJSON extends ServerResource {

	@Get("json")
    public String getName() {
		Gson gson = EventsRegistryWebApplication.GSON;
		
		try {
			String name = UsersAccessObject.getUserName(getAttribute("email"));
		
			return gson.toJson(name, String.class);   	
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
		}
    }
    
    @Put("json")
    public String updateName(String payload) {
    		Gson gson = EventsRegistryWebApplication.GSON;
		String email = getAttribute("email");
		
		try {
			if (!getClientInfo().getUser().getIdentifier().equals(getAttribute("email")))
				throw new UnauthorizedUserException("You are not authorized.");
			
			String name;
			try {
				name = gson.fromJson(payload, String.class);
			} catch (JsonSyntaxException e) {
	    			Status status = new Status(ErrorCodes.JSON_PARSING);
	    			setStatus(status);
	    			
	    			return gson.toJson(new JsonParsingException(e.getMessage()), JsonParsingException.class);
	    		}
			
			UsersAccessObject.updateUserName(email, name);
			
			return gson.toJson("Name updated for user with email " + email + ".", String.class);
		} catch (InvalidUserEmailException e) {
			Status status = new Status(ErrorCodes.INVALID_USER_EMAIL);
			setStatus(status);
			
			return gson.toJson(e, InvalidUserEmailException.class);
		} catch (UnauthorizedUserException e) {
			Status status = new Status(ErrorCodes.UNAUTHORIZED_USER);
			setStatus(status);
			
			return gson.toJson(e, UnauthorizedUserException.class);
		} catch (VoidClassFieldException e) {
			Status status = new Status(ErrorCodes.VOID_CLASS_FIELD);
			setStatus(status);
			
			return gson.toJson(e, VoidClassFieldException.class);
		} catch (GenericSQLException e) {
			Status status = new Status(ErrorCodes.GENERIC_SQL);
			setStatus(status);
			
			return gson.toJson(e, GenericSQLException.class);
		}
	}
}
