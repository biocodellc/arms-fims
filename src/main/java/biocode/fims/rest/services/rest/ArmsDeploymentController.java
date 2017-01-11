package biocode.fims.rest.services.rest;

import biocode.fims.arms.entities.Deployment;
import biocode.fims.arms.services.DeploymentService;
import biocode.fims.bcid.Identifier;
import biocode.fims.rest.FimsService;
import biocode.fims.service.OAuthProviderService;
import biocode.fims.settings.SettingsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * ARMS deployment services
 */

@Controller
@Path("deployments")
public class ArmsDeploymentController extends FimsService {

    private final DeploymentService deploymentService;

    @Autowired
    ArmsDeploymentController(DeploymentService deploymentService, SettingsManager settingsManager) {
        super(settingsManager);
        this.deploymentService = deploymentService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{expeditionId}/{deploymentId}")
    public Response getExpedition(@PathParam("expeditionId") int expeditionId,
                                  @PathParam("deploymentId") String deploymentId) {
        Deployment deployment = deploymentService.getDeployment(expeditionId, deploymentId);
        if (deployment == null)
            return Response.noContent().build();
        return Response.ok(deployment).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{identifier: .+}")
    public Response getExpedition(@PathParam("identifier") String identifierString) {
        String divider = settingsManager.retrieveValue("divider");
        Identifier identifier = new Identifier(identifierString, divider);

        Deployment deployment = deploymentService.getDeployment(identifier.getBcidIdentifier(), identifier.getSuffix());
        if (deployment != null)
            return Response.ok(deployment).build();
        return Response.noContent().build();
    }
}
