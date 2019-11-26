package server.web.resources.json;

import java.text.ParseException;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;

import server.backend.wrapper.EventsRegistryAPI;

public class EventsRegistrySizeJSON extends ServerResource {
	
	@Get
	public String getSize() throws ParseException {    	
		Gson gson = new Gson();
		EventsRegistryAPI erapi = EventsRegistryAPI.instance();
		
		return gson.toJson(new Integer(erapi.size()), Integer.class);
	}
}
