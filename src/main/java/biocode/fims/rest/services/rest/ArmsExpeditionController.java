package biocode.fims.rest.services.rest;

import biocode.fims.arms.entities.ArmsExpedition;
import biocode.fims.arms.services.ArmsExpeditionService;
import biocode.fims.bcid.Identifier;
import biocode.fims.config.ConfigurationFileFetcher;
import biocode.fims.digester.Mapping;
import biocode.fims.entities.Expedition;
import biocode.fims.rest.FimsService;
import biocode.fims.rest.filters.Authenticated;
import biocode.fims.service.BcidService;
import biocode.fims.service.OAuthProviderService;
import biocode.fims.service.ProjectService;
import biocode.fims.settings.SettingsManager;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.List;

/**
 * ARMS specific expedition services
 */

@Controller
@Path("arms/projects")
public class ArmsExpeditionController extends FimsService {

    private final ArmsExpeditionService armsExpeditionService;

    @Autowired
    ArmsExpeditionController(ArmsExpeditionService armsExpeditionService, SettingsManager settingsManager) {
        super(settingsManager);
        this.armsExpeditionService = armsExpeditionService;
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
        mapping.addMappingRules(configFile);

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

        armsExpeditionService.create(armsExpedition, userContext.getUser().getUserId(), projectId, mapping);

        return Response.ok(armsExpedition).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response list(@QueryParam("includePublic") @DefaultValue("false") boolean includePublic) {
        List<ArmsExpedition> expeditions;
        Integer projectId = Integer.valueOf(settingsManager.retrieveValue("projectId"));

        if (userContext.getUser() != null) {
            expeditions = armsExpeditionService.getArmsExpeditions(projectId, userContext.getUser().getUserId(), includePublic);
        } else {
            expeditions = armsExpeditionService.getPublicExpeditions(projectId);
        }
        return Response.ok(expeditions).build();
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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(ArmsExpedition.withDeploymentsView.class)
    @Path("{identifier: .+}")
    public Response getExpedition(@PathParam("identifier") String identifierString) {
        String divider = settingsManager.retrieveValue("divider");
        Identifier identifier = new Identifier(identifierString, divider);

        ArmsExpedition armsExpedition = armsExpeditionService.getArmsExpeditionByIdentifier(identifier.getBcidIdentifier());
        if (armsExpedition != null)
            return Response.ok(armsExpedition).build();
        return Response.noContent().build();
    }
}
