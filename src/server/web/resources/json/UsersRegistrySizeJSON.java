package server.web.resources.json;

import java.text.ParseException;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;

import server.backend.wrapper.UsersRegistryAPI;

public class UsersRegistrySizeJSON extends ServerResource {

	@Get
	public String getSize() throws ParseException {    	
		Gson gson = new Gson();
		UsersRegistryAPI urapi = UsersRegistryAPI.instance();
		
		return gson.toJson(new Integer(urapi.size()), Integer.class);
	}
}
