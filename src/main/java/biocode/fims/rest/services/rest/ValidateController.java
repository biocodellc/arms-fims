package biocode.fims.rest.services.rest;

import biocode.fims.application.config.FimsProperties;
import biocode.fims.arms.entities.ArmsExpedition;
import biocode.fims.arms.services.ArmsExpeditionService;
import biocode.fims.config.ConfigurationFileFetcher;
import biocode.fims.entities.Project;
import biocode.fims.fileManagers.AuxilaryFileManager;
import biocode.fims.fileManagers.fimsMetadata.FimsMetadataFileManager;
import biocode.fims.fimsExceptions.*;
import biocode.fims.fimsExceptions.BadRequestException;
import biocode.fims.rest.FimsService;
import biocode.fims.run.Process;
import biocode.fims.run.ProcessController;
import biocode.fims.service.*;
import biocode.fims.settings.SettingsManager;
import biocode.fims.utils.EmailUtils;
import biocode.fims.utils.FileUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.InputStream;
import java.util.*;

/**
 */
@Scope("prototype")
@Controller
@Path("validate")
public class ValidateController extends FimsService {
    private static final SettingsManager settingManager = SettingsManager.getInstance();
    private static final String uploadNotificationEmail = settingManager.retrieveValue("uploadNotificationEmail");

    private final ExpeditionService expeditionService;
    private final List<AuxilaryFileManager> fileManagers;
    private final FimsMetadataFileManager fimsMetadataFileManager;
    private final ProjectService projectService;
    private final ArmsExpeditionService armsExpeditionService;

    public ValidateController(ExpeditionService expeditionService, FimsMetadataFileManager fimsMetadataFileManager,
                       List<AuxilaryFileManager> fileManagers, FimsProperties props, ProjectService projectService,
                       ArmsExpeditionService armsExpeditionService) {
        super(props);
        this.expeditionService = expeditionService;
        this.fimsMetadataFileManager = fimsMetadataFileManager;
        this.fileManagers = fileManagers;
        this.projectService = projectService;
        this.armsExpeditionService = armsExpeditionService;
    }

    /**
     * service to validate a fimsMetadata against a project's rules
     *
     * @param projectId
     * @param expeditionCode
     * @param upload
     * @return
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JSONObject validate(@FormDataParam("projectId") Integer projectId,
                               @FormDataParam("expeditionCode") String expeditionCode,
                               @FormDataParam("public") @DefaultValue("false") boolean isPublic,
                               @FormDataParam("upload") @DefaultValue("false") boolean upload,
                               @FormDataParam("fimsMetadata") FormDataBodyPart fimsMetadata) {
        Map<String, Map<String, Object>> fmProps = new HashMap<>();
        JSONObject returnValue = new JSONObject();
        boolean closeProcess = true;
        boolean removeController = true;

        Project project = projectService.getProject(projectId, props.appRoot());

        if (project == null) {
            throw new BadRequestException("Project not found");
        }

        // create a new processController
        ProcessController processController = new ProcessController(projectId, expeditionCode);
        processController.setOutputFolder(defaultOutputDirectory());

        // place the processController in the session here so that we can track the status of the validation process
        // by calling biocode.fims.rest/validate/status
        session.setAttribute("processController", processController);

        try {
            // update the status
            processController.appendStatus("Initializing...<br>");

            // save the fimsMetadata and/or fasta files
            if (fimsMetadata != null && fimsMetadata.getContentDisposition().getFileName() != null) {
                String datasetFilename = fimsMetadata.getContentDisposition().getFileName();
                processController.appendStatus("\nFims Metadata filename = " + datasetFilename);

                InputStream is = fimsMetadata.getEntityAs(InputStream.class);
                String tempFilename = saveFile(is, datasetFilename, "xls");

                Map<String, Object> props = new HashMap<>();
                props.put("filename", tempFilename);

                fmProps.put(FimsMetadataFileManager.NAME, props);
            }

            File configFile = new ConfigurationFileFetcher(projectId, defaultOutputDirectory(), false).getOutputFile();

            // Create the process object --- this is done each time to orient the application
            Process process = new Process.ProcessBuilder(fimsMetadataFileManager, processController, props)
                    .addFileManagers(fileManagers)
                    .addFmProperties(fmProps)
                    .configFile(configFile)
                    .build();

            processController.setProcess(process);

            if (process.validate() && upload) {
                if (userContext.getUser() == null) {
                    throw new UnauthorizedRequestException("You must be logged in to upload.");
                }

                processController.setUserId(userContext.getUser().getUserId());

                // set public status to true in processController if user wants it on
                processController.setPublicStatus(isPublic);

                // if there were validation warnings and user would like to upload, we need to ask the user to continue
                if (processController.getHasWarnings()) {
                    returnValue.put("continue", processController.getMessages());

                    // there were no validation warnings and the user would like to upload, so continue
                } else {
                    JSONObject msg = new JSONObject();
                    msg.put("message", "continue");
                    returnValue.put("continue", msg);
                }

                // don't remove the controller or inputFiles as we will need it later for uploading this file
                closeProcess = false;
                removeController = false;

            } else {
                returnValue.put("done", processController.getMessages());
            }


            return returnValue;

        } finally {
            if (closeProcess && processController.getProcess() != null) {
                processController.getProcess().close();
            }
            if (removeController) {
                session.removeAttribute("processController");
            }

        }
    }

    /**
     * Service to upload a fimsMetadata to an expedition. The validate service must be called before this service.
     *
     * @return
     */
    @GET
    @Path("/continue")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JSONObject upload() {
        ProcessController processController = (ProcessController) session.getAttribute("processController");
        JSONObject returnValue = new JSONObject();
        boolean removeProcessController = true;

        // if no processController is found, we can't do anything
        if (processController == null) {
            //TODO throw exception with ErrorCode
            throw new BadRequestException("No process was detected");
        }

        try {

            // check if user is logged in
            if (processController.getUserId() == 0) {
                throw new BadRequestException("You must be logged in to upload");
            }

            Process p = processController.getProcess();

            p.upload(false, props.ignoreUser(), expeditionService);

            ArmsExpedition armsExpedition = armsExpeditionService.getArmsExpedition(processController.getExpeditionCode(), processController.getProjectId());

            String emailBody = armsExpedition.getPrincipalInvestigator() + " has uploaded data to project: " + armsExpedition.getExpedition().getExpeditionCode();

            // Send an Email that this completed
            EmailUtils.sendEmail(
                    uploadNotificationEmail,
                    "ARMS Data uploaded",
                    emailBody);

            returnValue.put("done", processController.getSuccessMessage());
            return returnValue;
        } finally {
            // remove the processController from the session
            if (removeProcessController) {
                session.removeAttribute("processController");
                processController.getProcess().close();
            }
        }
    }

    /**
     * Service used for getting the current status of the fimsMetadata validation/upload.
     *
     * @return
     */
    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public String status() {
        ProcessController processController = (ProcessController) session.getAttribute("processController");
        if (processController == null) {
            return "{\"error\": \"Waiting for validation to process...\"}";
        }

        return "{\"status\": \"" + processController.getStatusSB().toString() + "\"}";
    }

    private String saveFile(InputStream is, String filename, String defaultExt) {
        String ext = FileUtils.getExtension(filename, defaultExt);
        String tempFilename = FileUtils.saveTempFile(is, ext);
        if (tempFilename == null) {
            throw new FimsRuntimeException("Server error saving file: " + filename, 500);
        }

        return tempFilename;
    }
}
