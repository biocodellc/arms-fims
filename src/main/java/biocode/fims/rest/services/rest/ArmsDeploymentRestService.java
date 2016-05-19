package biocode.fims.rest.services.rest;

import biocode.fims.arms.entities.ArmsExpedition;
import biocode.fims.arms.entities.Deployment;
import biocode.fims.arms.services.ArmsExpeditionService;
import biocode.fims.arms.services.DeploymentService;
import biocode.fims.config.ConfigurationFileFetcher;
import biocode.fims.digester.Entity;
import biocode.fims.digester.Mapping;
import biocode.fims.entities.Expedition;
import biocode.fims.rest.FimsService;
import biocode.fims.rest.filters.Authenticated;
import biocode.fims.service.BcidService;
import biocode.fims.service.ProjectService;
import biocode.fims.service.UserService;
import biocode.fims.settings.SettingsManager;
import com.fasterxml.jackson.annotation.JsonView;
import org.apache.commons.digester3.Digester;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * ARMS deployment services
 */

@Path("deployments")
public class ArmsDeploymentRestService extends FimsService {

    private final DeploymentService deploymentService;

    @Autowired
    ArmsDeploymentRestService(DeploymentService deploymentService,
                              UserService userService, SettingsManager settingsManager) {
        super(userService, settingsManager);
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
}
