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
import exceptions.InvalidEventIdException;
import exceptions.UnauthorizedUserException;
import server.backend.EventsAccessObject;
import server.web.frontend.EventsRegistryWebApplication;

public class EventPhotoJSON extends ServerResource {
	
	@Get
    public Representation getPhoto() throws ResourceException {
		@SuppressWarnings("unused")
		Gson gson = EventsRegistryWebApplication.GSON;
		
		try {
    			int id = Integer.valueOf(getAttribute("id"));
			String photoPath = EventsAccessObject.getEventPhotoPath(id);
			
			Path path = Paths.get(EventsRegistryWebApplication.EVENTS_PHOTOS_DIRECTORY + photoPath);
			
			if (!new File(path.toString()).exists())
//				return gson.toJson("There is no photo for event with id " + getAttribute("id") + ".", String.class);
				return null;
			
			return new FileRepresentation(new File(path.toString()), MediaType.IMAGE_JPEG);
//			try {
//				getResponse().setEntity(new ByteArrayRepresentation(Files.readAllBytes(path), MediaType.IMAGE_JPEG));
//			} catch (IOException e) {
//				throw new ResourceException(e);
//			}
		} catch (InvalidEventIdException e) {
			Status status = new Status(ErrorCodes.INVALID_EVENT_ID);
			setStatus(status);
			
//			return gson.toJson(e, InvalidEventIdException.class);
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
		int id = Integer.valueOf(getAttribute("id"));
		
		try {
			String ownerEmail = EventsAccessObject.getEventOwnerEmail(id);
			
			if (!getClientInfo().getUser().getIdentifier().equals(ownerEmail))
				throw new UnauthorizedUserException("You are not authorized.");
			
			String photoPath = EventsAccessObject.getEventPhotoPath(id);
			
			try {
				entity.write(new FileOutputStream(new File(EventsRegistryWebApplication.EVENTS_PHOTOS_DIRECTORY + photoPath)));
			} catch (Exception e) {
				throw new ResourceException(e);
			}
			
//			return gson.toJson("Photo updated for event with id " + getAttribute("id") + ".", String.class);
			return gson.toJson(true, boolean.class);
		} catch (InvalidEventIdException e) {
			Status status = new Status(ErrorCodes.INVALID_EVENT_ID);
			setStatus(status);
			
			return gson.toJson(e, InvalidEventIdException.class);
		} catch (UnauthorizedUserException e) {
			Status status = new Status(ErrorCodes.UNAUTHORIZED_USER);
			setStatus(status);
			
			return gson.toJson(e, UnauthorizedUserException.class);
		} catch (GenericSQLException e) {
			Status status = new Status(ErrorCodes.GENERIC_SQL);
			setStatus(status);
			
//			return gson.toJson(e, GenericSQLException.class);
			return null;
		}
	}
}
