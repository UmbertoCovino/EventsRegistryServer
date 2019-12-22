package server.web.resources.json;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;

import exceptions.ErrorCodes;
import exceptions.GenericSQLException;
import exceptions.InvalidUserEmailException;
import exceptions.UnauthorizedUserException;
import exceptions.VoidClassFieldException;
import server.backend.UsersAccessObject;
import server.web.frontend.EventsRegistryWebApplication;

public class UserPhotoJSON extends ServerResource {

	@Get
    public Representation getPhoto() throws ResourceException {
		try {
			String photoPath = UsersAccessObject.getUserPhotoPath(getAttribute("email"));
			
			Path path = Paths.get(EventsRegistryWebApplication.USERS_PHOTOS_DIRECTORY + photoPath);
			
			if (!new File(path.toString()).exists())
				//return gson.toJson("There is no photo for user with email " + getAttribute("email") + ".", String.class);
				return null;
			
			return new FileRepresentation(new File(path.toString()), MediaType.IMAGE_JPEG);
//			try {
//				getResponse().setEntity(new ByteArrayRepresentation(Files.readAllBytes(path), MediaType.IMAGE_JPEG));
//			} catch (IOException e) {
//				throw new ResourceException(e);
//			}
		} catch (InvalidUserEmailException e) {
			Status status = new Status(ErrorCodes.INVALID_USER_EMAIL);
			setStatus(status);
			
//			return gson.toJson(e, InvalidUserEmailException.class);
			return null;
		} catch (VoidClassFieldException e) {
			Status status = new Status(ErrorCodes.VOID_CLASS_FIELD);
			setStatus(status);
			
			//return gson.toJson(e, VoidClassFieldException.class);
			return null;
		} catch (GenericSQLException e) {
			Status status = new Status(ErrorCodes.GENERIC_SQL);
			setStatus(status);
			
//			return gson.toJson(e, GenericSQLException.class);
			return null;
		}
    }
    
    @Put
    public String updatePhoto(Representation entity) throws ResourceException {
    		Gson gson = EventsRegistryWebApplication.GSON;
		String email = getAttribute("email");
		
		try {
			if (!getClientInfo().getUser().getIdentifier().equals(email))
				throw new UnauthorizedUserException("You are not authorized.");

			String photoPath = UsersAccessObject.getUserPhotoPath(getAttribute("email"));
			
			try {
				entity.write(new FileOutputStream(new File(EventsRegistryWebApplication.USERS_PHOTOS_DIRECTORY + photoPath)));
			} catch (Exception e) {
				throw new ResourceException(e);
			}
			
//			return gson.toJson("Photo updated for user with email " + getAttribute("email") + ".", String.class);
			return gson.toJson(true, boolean.class);
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
			
//			return gson.toJson(e, GenericSQLException.class);
			return null;
		}
	}
}
