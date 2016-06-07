package biocode.fims.rest.services.rest;

import biocode.fims.arms.entities.Deployment;
import biocode.fims.arms.services.DeploymentService;
import biocode.fims.mysql.query.DeploymentsWriter;
import biocode.fims.mysql.query.Operator;
import biocode.fims.mysql.query.Query;
import biocode.fims.mysql.query.SearchCriteria;
import biocode.fims.rest.FimsService;
import biocode.fims.service.OAuthProviderService;
import biocode.fims.settings.SettingsManager;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

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
    public Iterable<Deployment> queryJson(Query query) {
        return deploymentService.query(query);
    }

    @Path("excel")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response queryExcel(Query query) {
        int projectId = Integer.parseInt(settingsManager.retrieveValue("projectId"));
        List<Deployment> deployments = (List<Deployment>) deploymentService.query(query);

        DeploymentsWriter deploymentsWriter = new DeploymentsWriter(deployments, uploadPath(), projectId);
        return Response
                .ok(deploymentsWriter.writeExcel())
                .header("Content-Disposition", "attachment; filename=arms-fims-output.xlsx")
                .build();
    }

    @Path("tab")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response queryTab(Query query) {
        int projectId = Integer.parseInt(settingsManager.retrieveValue("projectId"));
        List<Deployment> deployments = (List<Deployment>) deploymentService.query(query);

        DeploymentsWriter deploymentsWriter = new DeploymentsWriter(deployments, uploadPath(), projectId);
        return Response
                .ok(deploymentsWriter.writeTabDelimitedText())
                .header("Content-Disposition", "attachment; filename=arms-fims-output.txt")
                .build();
    }
}