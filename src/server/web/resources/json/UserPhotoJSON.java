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

import commons.ErrorCodes;
import commons.InvalidUserEmailException;
import commons.UnauthorizedUserException;
import commons.User;
import server.backend.wrapper.UsersRegistryAPI;

public class UserPhotoJSON extends ServerResource {

	@Get
    public Representation getPhoto() throws ResourceException {
		UsersRegistryAPI urapi = UsersRegistryAPI.instance();
		
		try {
			User user = urapi.get(getAttribute("email"));
			
			Path path = Paths.get(urapi.getPhotosDirectory() + user.getPhotoPath());
			
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
		}
    }
    
    @Put
    public String updatePhoto(Representation entity) throws ResourceException {
		Gson gson = new Gson();
		UsersRegistryAPI urapi = UsersRegistryAPI.instance();
		
		try {
			if (!getClientInfo().getUser().getIdentifier().equals(getAttribute("email")))
				throw new UnauthorizedUserException("You are not authorized.");
			
			User user = urapi.get(getAttribute("email"));
			
			try {
				entity.write(new FileOutputStream(new File(urapi.getPhotosDirectory() + user.getPhotoPath())));
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
		}
	}
}
