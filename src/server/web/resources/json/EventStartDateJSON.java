package server.web.resources.json;

import java.text.ParseException;
import java.util.Date;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;

import commons.ErrorCodes;
import commons.GenericSQLException;
import commons.InvalidEventIdException;
import commons.UnauthorizedUserException;
import commons.VoidClassFieldException;
import server.backend.EventsAccessObject;

public class EventStartDateJSON extends ServerResource {
	
	@Get
    public String getStartDate() throws ParseException {
		Gson gson = new Gson();
		
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
    
    @Put
    public String updateStartDate(String payload) {
		Gson gson = new Gson();
		int id = Integer.valueOf(getAttribute("id"));
		
		try {
			String ownerEmail = EventsAccessObject.getEventOwnerEmail(id);
			
			if (!getClientInfo().getUser().getIdentifier().equals(ownerEmail))
				throw new UnauthorizedUserException("You are not authorized.");
			
			Date startDate = gson.fromJson(payload, Date.class);
			
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