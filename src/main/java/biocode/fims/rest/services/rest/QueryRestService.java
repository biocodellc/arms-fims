package biocode.fims.rest.services.rest;

import biocode.fims.arms.entities.Deployment;
import biocode.fims.arms.services.DeploymentService;
import biocode.fims.arms.query.DeploymentsWriter;
import biocode.fims.arms.utils.BerkeleyMapperHelper;
import biocode.fims.config.ConfigurationFileFetcher;
import biocode.fims.digester.Mapping;
import biocode.fims.digester.Validation;
import biocode.fims.mysql.query.Query;
import biocode.fims.rest.FimsService;
import biocode.fims.service.OAuthProviderService;
import biocode.fims.settings.SettingsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.List;

/**
 * Arms query REST services
 */
@Controller
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

        File configFile = new ConfigurationFileFetcher(projectId, uploadPath(), false).getOutputFile();
        Mapping mapping = new Mapping();
        mapping.addMappingRules(configFile);

        String defaultSheetName = mapping.getDefaultSheetName();

        DeploymentsWriter deploymentsWriter = new DeploymentsWriter(
                deployments,
                uploadPath(),
                mapping.getAllAttributes(defaultSheetName),
                defaultSheetName);

        return Response
                .ok(deploymentsWriter.writeExcel(projectId))
                .header("Content-Disposition", "attachment; filename=arms-fims-output.xlsx")
                .build();
    }

    @Path("tab")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response queryTabForBerkeleyMapper(Query query) {
        int projectId = Integer.parseInt(settingsManager.retrieveValue("projectId"));
        List<Deployment> deployments = (List<Deployment>) deploymentService.query(query);

        File configFile = new ConfigurationFileFetcher(projectId, uploadPath(), false).getOutputFile();
        Mapping mapping = new Mapping();
        mapping.addMappingRules(configFile);

        String defaultSheetName = mapping.getDefaultSheetName();

        DeploymentsWriter deploymentsWriter = new DeploymentsWriter(
                deployments,
                uploadPath(),
                BerkeleyMapperHelper.getAttributes(mapping.getAllAttributes(defaultSheetName)),
                defaultSheetName);

        return Response
                .ok(deploymentsWriter.writeTabDelimitedText())
                .header("Content-Disposition", "attachment; filename=arms-fims-output.txt")
                .build();
    }
}
