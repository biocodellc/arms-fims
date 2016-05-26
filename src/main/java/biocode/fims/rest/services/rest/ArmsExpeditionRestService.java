package biocode.fims.rest.services.rest;

import biocode.fims.arms.entities.ArmsExpedition;
import biocode.fims.arms.services.ArmsExpeditionService;
import biocode.fims.config.ConfigurationFileFetcher;
import biocode.fims.digester.Entity;
import biocode.fims.digester.Mapping;
import biocode.fims.entities.Expedition;
import biocode.fims.rest.FimsService;
import biocode.fims.rest.filters.Authenticated;
import biocode.fims.service.BcidService;
import biocode.fims.service.OAuthProviderService;
import biocode.fims.service.ProjectService;
import biocode.fims.settings.SettingsManager;
import com.fasterxml.jackson.annotation.JsonView;
import org.apache.commons.digester3.Digester;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * ARMS specific expedition services
 */

@Path("arms/projects")
public class ArmsExpeditionRestService extends FimsService {

    private final ProjectService projectService;
    private final ArmsExpeditionService armsExpeditionService;
    private final BcidService bcidService;

    @Autowired
    ArmsExpeditionRestService(ProjectService projectService, ArmsExpeditionService armsExpeditionService, BcidService bcidService,
                              OAuthProviderService providerService, SettingsManager settingsManager) {
        super(providerService, settingsManager);
        this.projectService = projectService;
        this.armsExpeditionService = armsExpeditionService;
        this.bcidService = bcidService;
    }

    @POST
    @Authenticated
    public Response create(@FormParam("principalInvestigator") String principalInvestigator,
                           @FormParam("contactName") String contactName,
                           @FormParam("contactEmail") String contactEmail,
                           @FormParam("fundingSource") String fundingSource,
                           @FormParam("envisionedDuration") int envisionedDuration,
                           @FormParam("geographicScope") String geographicScope,
                           @FormParam("goals") String goals,
                           @FormParam("leadOrganization") String leadOrganization,
                           @FormParam("expeditionCode") String expeditionCode,
                           @FormParam("public") @DefaultValue("true") boolean isPublic) {

        int projectId = Integer.parseInt(settingsManager.retrieveValue("projectId"));

        File configFile = new ConfigurationFileFetcher(projectId, uploadPath(), false).getOutputFile();
        Mapping mapping = new Mapping();
        mapping.addMappingRules(new Digester(), configFile);

        Expedition expedition = new Expedition.ExpeditionBuilder(expeditionCode)
                .isPublic(isPublic)
                .build();
        ArmsExpedition armsExpedition = new ArmsExpedition.ArmsExpeditionBuilder(principalInvestigator, expedition)
                .contactName(contactName)
                .contactEmail(contactEmail)
                .fundingSource(fundingSource)
                .envisionedDuration(envisionedDuration)
                .geographicScope(geographicScope)
                .goals(goals)
                .leadOrganization(leadOrganization)
                .build();

        armsExpeditionService.create(armsExpedition, user.getUserId(), projectId, mapping);

        return Response.ok(armsExpedition).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response list() {
        return Response.ok(armsExpeditionService.findAll()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(ArmsExpedition.withDeploymentsView.class)
    @Path("{expeditionId}/")
    public Response getExpedition(@PathParam("expeditionId") int expeditionId) {
        ArmsExpedition armsExpedition = armsExpeditionService.getArmsExpedition(expeditionId);
        if (armsExpedition != null)
            return Response.ok(armsExpedition).build();
        return Response.noContent().build();
    }
}
