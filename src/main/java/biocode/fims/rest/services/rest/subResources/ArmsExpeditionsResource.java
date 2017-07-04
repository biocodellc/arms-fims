package biocode.fims.rest.services.rest.subResources;

import biocode.fims.application.config.FimsProperties;
import biocode.fims.arms.services.ArmsExpeditionService;
import biocode.fims.config.ConfigurationFileFetcher;
import biocode.fims.digester.Mapping;
import biocode.fims.fileManagers.fimsMetadata.FimsMetadataFileManager;
import biocode.fims.fimsExceptions.ForbiddenRequestException;
import biocode.fims.rest.AcknowledgedResponse;
import biocode.fims.rest.UserEntityGraph;
import biocode.fims.rest.filters.Admin;
import biocode.fims.rest.filters.Authenticated;
import biocode.fims.run.ProcessController;
import biocode.fims.service.ExpeditionService;
import biocode.fims.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.File;

/**
 * @author rjewing
 */
@Scope("prototype")
@Controller
@Produces(MediaType.APPLICATION_JSON)
public class ArmsExpeditionsResource extends ExpeditionsResource {

    private final ArmsExpeditionService armsExpeditionService;
    private ProjectService projectService;
    FimsMetadataFileManager fimsMetadataFileManager;

    @Autowired
    public ArmsExpeditionsResource(ExpeditionService expeditionService, ProjectService projectService,
                                   FimsProperties props, FimsMetadataFileManager fimsMetadataFileManager,
                                   ArmsExpeditionService armsExpeditionService) {
        super(expeditionService, projectService, props, fimsMetadataFileManager);
        this.armsExpeditionService = armsExpeditionService;
        this.projectService = projectService;
        this.fimsMetadataFileManager = fimsMetadataFileManager;
    }

    /**
     * delete an expedition
     * <p>
     * Project Admin access only
     *
     * @param projectId      The projectId the expedition belongs to
     * @param expeditionCode The expeditionCode of the expedition to delete
     * @responseMessage 403 not the project's admin `biocode.fims.utils.ErrorInfo
     */
    @Override
    @UserEntityGraph("User.withProjects")
    @DELETE
    @Authenticated
    @Admin
    @Path("/{expeditionCode}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public AcknowledgedResponse deleteExpedition(@PathParam("projectId") Integer projectId,
                                                 @PathParam("expeditionCode") String expeditionCode) {
        if (!projectService.isProjectAdmin(userContext.getUser(), projectId)) {
            throw new ForbiddenRequestException("You are not an admin for this project");
        }

        armsExpeditionService.delete(expeditionCode, projectId);

        // delete any data for the expedition
        File configFile = new ConfigurationFileFetcher(projectId, defaultOutputDirectory(), true).getOutputFile();

        Mapping mapping = new Mapping();
        mapping.addMappingRules(configFile);

        ProcessController processController = new ProcessController(projectId, expeditionCode);
        processController.setOutputFolder(defaultOutputDirectory());
        processController.setMapping(mapping);
        fimsMetadataFileManager.setProcessController(processController);
        fimsMetadataFileManager.deleteDataset();

        return new AcknowledgedResponse(true);
    }
}
