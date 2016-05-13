package biocode.fims.rest.services.rest;

import biocode.fims.arms.entities.ArmsExpedition;
import biocode.fims.arms.services.ArmsExpeditionService;
import biocode.fims.entities.Expedition;
import biocode.fims.entities.Project;
import biocode.fims.fimsExceptions.*;
import biocode.fims.rest.FimsService;
import biocode.fims.rest.filters.Authenticated;
import biocode.fims.service.ProjectService;
import biocode.fims.service.UserService;
import biocode.fims.settings.SettingsManager;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * ARMS specific expedition services
 */

@Path("arms/projects")
public class ArmsExpeditionRestService extends FimsService {

    private final ProjectService projectService;
    private final ArmsExpeditionService armsExpeditionService;

    @Autowired
    ArmsExpeditionRestService(ProjectService projectService, ArmsExpeditionService armsExpeditionService,
                              UserService userService, SettingsManager settingsManager) {
        super(userService, settingsManager);
        this.projectService = projectService;
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
                           @FormParam("public") boolean isPublic) {

        Project project = projectService.getProject(Integer.parseInt(settingsManager.retrieveValue("projectId")));
        Expedition expedition = new Expedition.ExpeditionBuilder(expeditionCode, user, project)
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

        armsExpeditionService.create(armsExpedition);

        return Response.ok(armsExpedition).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response list() {
        return Response.ok(armsExpeditionService.findAll()).build();
    }
}
