package server.web.resources.json;

import java.text.ParseException;
import java.util.Date;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import commons.exceptions.ErrorCodes;
import commons.exceptions.GenericSQLException;
import commons.exceptions.InvalidEventIdException;
import commons.exceptions.JsonParsingException;
import commons.exceptions.UnauthorizedUserException;
import commons.exceptions.VoidClassFieldException;
import server.backend.EventsAccessObject;
import server.web.frontend.EventsRegistryWebApplication;

public class EventStartDateJSON extends ServerResource {
	
	@Get("json")
    public String getStartDate() throws ParseException {
		Gson gson = EventsRegistryWebApplication.GSON;
		
		try {
			Date startDate = EventsAccessObject.getEventStartDate(Integer.valueOf(getAttribute("id")));
		
			return gson.toJson(startDate, Date.class);
		} catch (InvalidEventIdException e) {
			Status status = new Status(ErrorCodes.INVALID_EVENT_ID);
			setStatus(status);
			
			return gson.toJson(e, InvalidEventIdException.class);
		} catch (GenericSQLException e) {
			Status status = new Status(ErrorCodes.GENERIC_SQL);
			setStatus(status);
			
			return gson.toJson(e, GenericSQLException.class);
		}
    }
    
    @Put("json")
    public String updateStartDate(String payload) {
    		Gson gson = EventsRegistryWebApplication.GSON;
		int id = Integer.valueOf(getAttribute("id"));
		
		try {
			String ownerEmail = EventsAccessObject.getEventOwnerEmail(id);
			
			if (!getClientInfo().getUser().getIdentifier().equals(ownerEmail))
				throw new UnauthorizedUserException("You are not authorized.");
			
			Date startDate;
			try {
				startDate = gson.fromJson(payload, Date.class);
			} catch (JsonSyntaxException e) {
				Status status = new Status(ErrorCodes.JSON_PARSING);
				setStatus(status);
				
				return gson.toJson(new JsonParsingException(e.getMessage()), JsonParsingException.class);
			}
			
			EventsAccessObject.updateEventStartDate(id, startDate);
			
			return gson.toJson("Start date updated for event with id " + id + ".", String.class);
		} catch (InvalidEventIdException e) {
			Status status = new Status(ErrorCodes.INVALID_EVENT_ID);
			setStatus(status);
			
			return gson.toJson(e, InvalidEventIdException.class);
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
