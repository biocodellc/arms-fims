package biocode.fims.rest.services.rest;

import biocode.fims.rest.FimsService;
import biocode.fims.service.UserService;
import biocode.fims.settings.SettingsManager;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * arms-Fims utility services
 */
@Path("utils/")
public class ArmsUtils extends FimsService {

    ArmsUtils(UserService userService, SettingsManager settingsManager) {
        super(userService, settingsManager);
    }

    @GET
    @Path("/getMapboxToken")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMapboxToken() {
        String token = settingsManager.retrieveValue("mapboxAccessToken");

        return Response.ok("{\"accessToken\": \"" + token + "\"}").build();
    }
}