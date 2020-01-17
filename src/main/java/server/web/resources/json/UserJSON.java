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
import com.google.gson.JsonSyntaxException;

import commons.User;
import commons.exceptions.ErrorCodes;
import commons.exceptions.GenericSQLException;
import commons.exceptions.InvalidUserEmailException;
import commons.exceptions.JsonParsingException;
import commons.exceptions.UnauthorizedUserException;
import commons.exceptions.VoidClassFieldException;
import server.backend.UsersAccessObject;
import server.web.frontend.EventsRegistryWebApplication;

public class UserJSON extends ServerResource {

	@Get("json")
    public String getUser() throws ParseException {
		Gson gson = EventsRegistryWebApplication.GSON;
		
		try {
			User user = UsersAccessObject.getUser(getAttribute("email"));
			
			return gson.toJson(user, User.class);
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
	
	@Post("json")
    public String getUserIfPasswordIsCorrect(String payload) throws ParseException {
		Gson gson = EventsRegistryWebApplication.GSON;

		String password;
		try {
			password = gson.fromJson(payload, String.class);
		} catch (JsonSyntaxException e) {
			Status status = new Status(ErrorCodes.JSON_PARSING);
			setStatus(status);
			
			return gson.toJson(new JsonParsingException(e.getMessage()), JsonParsingException.class);
		}
		
		try {
			User user = UsersAccessObject.getUserWithPassword(getAttribute("email"));
			
			if (password != null && password.equals(user.getPassword())) // valutare l'opzione di restituire un booleano
				return gson.toJson(user.cloneWithoutPassword(), User.class);
			else {
				Status status = new Status(404);
				setStatus(status);
				
				return gson.toJson(null, User.class);
			}
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
    
    @Delete("json")
    public String deleteUser() {
    		Gson gson = EventsRegistryWebApplication.GSON;
		String email = getAttribute("email");
		
		try {
			if (!getClientInfo().getUser().getIdentifier().equals(email))
				throw new UnauthorizedUserException("You are not authorized.");
			
			String photoPath = UsersAccessObject.getUserPhotoPath(email);

			new File(EventsRegistryWebApplication.USERS_PHOTOS_DIRECTORY + "/" + photoPath).delete();
			
			UsersAccessObject.removeUser(getAttribute("email"));
			
			((MapVerifier) getContext().getDefaultVerifier()).getLocalSecrets().remove(email);
			
			return gson.toJson("User with email " + email + " removed.", String.class);
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
