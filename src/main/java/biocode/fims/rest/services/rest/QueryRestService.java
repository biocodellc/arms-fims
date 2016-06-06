package biocode.fims.rest.services.rest;

import biocode.fims.arms.entities.Deployment;
import biocode.fims.arms.services.DeploymentService;
import biocode.fims.mysql.query.Query;
import biocode.fims.rest.FimsService;
import biocode.fims.service.OAuthProviderService;
import biocode.fims.settings.SettingsManager;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Arms query REST services
 */
@Path("deployments/query")
public class QueryRestService extends FimsService {
    private final DeploymentService deploymentService;

    @Autowired
    public QueryRestService(DeploymentService deploymentService,
                            OAuthProviderService providerService, SettingsManager settingsManager) {
        super(providerService, settingsManager);
        this.deploymentService = deploymentService;
    }

    @Path("json")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Iterable<Deployment> queryJsonAsPost(Query query) {
        return deploymentService.query(query);
    }
}
