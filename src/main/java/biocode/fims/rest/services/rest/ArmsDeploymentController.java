package biocode.fims.rest.services.rest;

import biocode.fims.application.config.FimsProperties;
import biocode.fims.arms.entities.Deployment;
import biocode.fims.arms.services.DeploymentService;
import biocode.fims.bcid.Identifier;
import biocode.fims.rest.FimsService;
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
    ArmsDeploymentController(DeploymentService deploymentService, FimsProperties props) {
        super(props);
        this.deploymentService = deploymentService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{expeditionId}/{deploymentId}")
    public Response getDeployment(@PathParam("expeditionId") int expeditionId,
                                  @PathParam("deploymentId") String deploymentId) {
        Deployment deployment = deploymentService.getDeployment(expeditionId, deploymentId);
        if (deployment == null)
            return Response.noContent().build();
        return Response.ok(deployment).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{identifier: .+}")
    public Response getDeployment(@PathParam("identifier") String identifierString) {
        String divider = props.divider();
        Identifier identifier = new Identifier(identifierString, divider);

        Deployment deployment = deploymentService.getDeployment(identifier.getBcidIdentifier(), identifier.getSuffix());
        if (deployment != null)
            return Response.ok(deployment).build();
        return Response.noContent().build();
    }
}
