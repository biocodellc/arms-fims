package biocode.fims.rest.services.rest;

import biocode.fims.bcid.ResourceTypes;
import biocode.fims.config.ConfigurationFileTester;
import biocode.fims.digester.Attribute;
import biocode.fims.entities.Bcid;
import biocode.fims.entities.Expedition;
import biocode.fims.fimsExceptions.*;
import biocode.fims.fimsExceptions.BadRequestException;
import biocode.fims.mysql.MySqlDatasetTableValidator;
import biocode.fims.mysql.MySqlUploader;
import biocode.fims.reader.CsvTabularDataConverter;
import biocode.fims.reader.plugins.TabularDataReader;
import biocode.fims.renderers.Message;
import biocode.fims.renderers.RowMessage;
import biocode.fims.rest.FimsService;
import biocode.fims.run.Process;
import biocode.fims.run.ProcessController;
import biocode.fims.service.*;
import biocode.fims.settings.SettingsManager;
import biocode.fims.tools.ServerSideSpreadsheetTools;
import org.apache.commons.io.FilenameUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 */
@Path("validate")
public class Validate extends FimsService {

    private final MySqlUploader mySqlUploader;
    private final MySqlDatasetTableValidator mySqlDatasetTableValidator;
    private final ExpeditionService expeditionService;
    private final BcidService bcidService;

    @Autowired
    public Validate(MySqlUploader mySqlUploader, MySqlDatasetTableValidator mySqlDatasetTableValidator,
                    ExpeditionService expeditionService, BcidService bcidService,
                    OAuthProviderService providerService, SettingsManager settingsManager) {
        super(providerService, settingsManager);
        this.mySqlUploader = mySqlUploader;
        this.mySqlDatasetTableValidator = mySqlDatasetTableValidator;
        this.expeditionService = expeditionService;
        this.bcidService = bcidService;
    }
    /**
     * service to validate a dataset against a project's rules
     *
     * @param projectId
     * @param expeditionCode
     * @param upload
     * @param datasetIs
     * @param datasetFileData
     *
     * @return
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public String validate(@FormDataParam("projectId") Integer projectId,
                           @FormDataParam("expeditionCode") String expeditionCode,
                           @FormDataParam("upload") String upload,
                           @FormDataParam("public_status") String publicStatus,
                           @FormDataParam("dataset") InputStream datasetIs,
                           @FormDataParam("dataset") FormDataContentDisposition datasetFileData) {
        StringBuilder retVal = new StringBuilder();
        Boolean removeController = true;
        Boolean deleteInputFile = true;
        String inputFile;
        if (datasetFileData.getFileName() == null) {
            throw new BadRequestException("dataset is null");

        }

        // create a new processController
        ProcessController processController = new ProcessController(projectId, expeditionCode);

        // place the processController in the session here so that we can track the status of the validation process
        // by calling rest/validate/status
        session.setAttribute("processController", processController);


        // update the status
        processController.appendStatus("Initializing...<br>");
        // save the dataset
        processController.appendStatus("inputFilename = " + processController.stringToHTMLJSON(
                datasetFileData.getFileName()) + "<br>");

        // Save the uploaded dataset file
        String splitArray[] = datasetFileData.getFileName().split("\\.");
        String ext;
        if (splitArray.length == 0) {
            // if no extension is found, then guess
            ext = "xls";
        } else {
            ext = splitArray[splitArray.length - 1];
        }
        inputFile = processController.saveTempFile(datasetIs, ext);

        // if inputFile null, then there was an error saving the file
        if (inputFile == null) {
            throw new FimsRuntimeException("Server error saving dataset file.", 500);
        }
        processController.setInputFilename(inputFile);

        // Create the process object --- this is done each time to orient the application
        Process p = new Process(
                uploadPath(),
                processController,
                expeditionService
        );

        // Test the configuration file to see that we're good to go...
        ConfigurationFileTester cFT = new ConfigurationFileTester(p.configFile);
        boolean configurationGood = true;

        if (!cFT.isValidConfig()) {
//            String message = "<br>CONFIGURATION FILE ERROR...<br>Please talk to your project administrator to fix the following error:<br>\t\n";
//            message += cFT.getMessages();
            processController.setHasErrors(true);
            processController.setValidated(false);
//            processController.appendStatus(message + "<br>");
            configurationGood = false;
            retVal.append("{\"done\": ");
            JSONObject messages = new JSONObject();
            messages.put("config", cFT.getMessages());
            retVal.append(messages.toJSONString());
            retVal.append("}");
        }


        // Run the process only if the configuration is good.
        if (configurationGood) {
            processController.appendStatus("Validating...<br>");

            p.runValidation();
            if (!mySqlDatasetTableValidator.validate(processController.getMapping())) {
                processController.setHasErrors(true);
                processController.addMessage(processController.getMapping().getDefaultSheetName(),
                        new RowMessage("database and configuration file need to be synced", "System Error", Message.ERROR));
            }

            // if there were validation errors, we can't upload
            if (processController.getHasErrors()) {
                retVal.append("{\"done\": ");
                retVal.append(processController.getMessages().toJSONString());
                retVal.append("}");

            } else if (upload != null && upload.equals("on")) {

                if (user == null) {
                    throw new UnauthorizedRequestException("You must be logged in to upload.");
                }

                processController.setUserId(user.getUserId());

                // set public status to true in processController if user wants it on
                if (publicStatus != null && publicStatus.equals("on")) {
                       processController.setPublicStatus(true);
                }

                // if there were validation warnings and user would like to upload, we need to ask the user to continue
                if (processController.getHasWarnings()) {
                    retVal.append("{\"continue\": ");
                    retVal.append(processController.getMessages().toJSONString());
                    retVal.append("}");

                    // there were no validation warnings and the user would like to upload, so continue
                } else {
                    retVal.append("{\"continue\": {\"message\": \"continue\"}}");
                }

                // don't delete the inputFile because we'll need it for uploading
                deleteInputFile = false;

                // don't remove the controller as we will need it later for uploading this file
                removeController = false;
            } else {
                // User doesn't want to upload. Return the validation results
                retVal.append("{\"done\": ");
                retVal.append(processController.getMessages().toJSONString());
                retVal.append("}");
            }
        }

        if (deleteInputFile) {
            new File(inputFile).delete();
        }
        if (removeController) {
            session.removeAttribute("processController");
        }

        return retVal.toString();
    }

    /**
     * Service to upload a dataset to an expedition. The validate service must be called before this service.
     *
     * @return
     */
    @GET
    @Path("/continue")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public String upload() {
        String successMessage;
        ProcessController processController = (ProcessController) session.getAttribute("processController");

        // if no processController is found, we can't do anything
        if (processController == null) {
            return "{\"error\": \"No process was detected.\"}";
        }

        // check if user is logged in
        if (processController.getUserId() == 0) {
            return "{\"error\": \"You must be logged in to upload.\"}";
        }

        // if the process controller was stored in the session, then the user wants to continue, set warning cleared
        processController.setClearedOfWarnings(true);
        processController.setValidated(true);

        // Create the process object --- this is done each time to orient the application
        Process p = new Process(
                uploadPath(),
                processController,
                expeditionService
        );

        String outputPrefix = processController.getExpeditionCode() + "_output";

        Expedition expedition = p.runExpeditionCheck();

        if (!processController.isExpeditionAssignedToUserAndExists()) {
            throw new BadRequestException("Either the Arms Project doesn't exits, or you do not own the expedition.");
        }

        String destination = uploadPath();

        TabularDataReader tdr = p.getTabularDataReader();

        // convert the dataset to csv file for uploading
        CsvTabularDataConverter csvTabularDataConverter = new CsvTabularDataConverter(
                tdr, destination, outputPrefix);

        List<String> acceptableColumns = new LinkedList<>();
        List<String> acceptableColumnsInternal = new LinkedList<>();
        for (Attribute attribute : processController.getMapping().getAllAttributes(processController.getMapping().getDefaultSheetName())) {
            acceptableColumns.add(attribute.getColumn());
            acceptableColumnsInternal.add(attribute.getColumn_internal());
        }
        csvTabularDataConverter.convert(acceptableColumns, p.getMapping().getDefaultSheetName());

        tdr.closeFile();

        // upload the dataset
        mySqlUploader.execute(expedition.getExpeditionId(), acceptableColumnsInternal, csvTabularDataConverter.getCsvFile().getPath());

        boolean ezidRequest = Boolean.valueOf(settingsManager.retrieveValue("ezidRequests"));

        // Mint the data group
        Bcid bcid = new Bcid.BcidBuilder(ResourceTypes.DATASET_RESOURCE_TYPE)
                .title(processController.getExpeditionCode())
                .finalCopy(processController.getFinalCopy())
                .ezidRequest(ezidRequest)
                .build();

        bcidService.create(bcid, user.getUserId());
        bcidService.attachBcidToExpedition(bcid, expedition.getExpeditionId());

        // save the spreadsheet on the server
        File inputFile = new File(processController.getInputFilename());
        String ext = FilenameUtils.getExtension(inputFile.getName());
        String filename = "bcid_id_" + bcid.getBcidId() + "." + ext;
        File outputFile = new File(settingsManager.retrieveValue("serverRoot") + filename);

        ServerSideSpreadsheetTools serverSideSpreadsheetTools = new ServerSideSpreadsheetTools(inputFile);
        serverSideSpreadsheetTools.write(outputFile);

        bcid.setSourceFile(filename);
        bcidService.update(bcid);

        // delete the temporary file now that it has been uploaded
        inputFile.delete();

        successMessage = "<br><font color=#188B00>Successfully Uploaded!</font><br><br>";
        processController.appendStatus(successMessage);

        // remove the processController from the session
        session.removeAttribute("processController");

        return "{\"done\": {\"message\": \"" + JSONObject.escape(successMessage) + "\"}}";
    }

    /**
     * Service used for getting the current status of the dataset validation/upload.
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
}


