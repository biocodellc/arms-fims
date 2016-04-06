package biocode.fims.rest.services.rest;

import biocode.fims.bcid.Bcid;
import biocode.fims.bcid.BcidMinter;
import biocode.fims.bcid.ExpeditionMinter;
import biocode.fims.bcid.Resolver;
import biocode.fims.config.ConfigurationFileTester;
import biocode.fims.digester.Attribute;
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
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 */
@Path("validate")
public class Validate extends FimsService {

    @Autowired
    private MySqlUploader mySqlUploader;

    @Autowired
    private MySqlDatasetTableValidator mySqlDatasetTableValidator;

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
                processController
        );

        // Test the configuration file to see that we're good to go...
        ConfigurationFileTester cFT = new ConfigurationFileTester();
        boolean configurationGood = true;

        cFT.init(p.configFile);

        if (!cFT.checkUniqueKeys()) {
            String message = "<br>CONFIGURATION FILE ERROR...<br>Please talk to your project administrator to fix the following error:<br>\t\n";
            message += cFT.getMessages();
            processController.setHasErrors(true);
            processController.setValidated(false);
            processController.appendStatus(message + "<br>");
            configurationGood = false;
            retVal.append("{\"done\": \"");
            retVal.append(processController.getStatusSB().toString());
            retVal.append("\"}");
        }


        // Run the process only if the configuration is good.
        if (configurationGood) {
            processController.appendStatus("Validating...<br>");

            p.runValidation();
            if (mySqlDatasetTableValidator.validate(processController.getMapping())) {
                processController.addMessage(processController.getMapping().getDefaultSheetName(),
                        new RowMessage("database and configuration file need to be synced", "System Error", Message.ERROR));
            }

            // if there were validation errors, we can't upload
            if (processController.getHasErrors()) {
                retVal.append("{\"done\": ");
                retVal.append(processController.getMessages().toJSONString());
                retVal.append("}");

            } else if (upload != null && upload.equals("on")) {

                if (username == null) {
                    throw new UnauthorizedRequestException("You must be logged in to upload.");
                }

                processController.setUserId(userId);

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
     * @param createExpedition
     *
     * @return
     */
    @GET
    @Path("/continue")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public String upload(@QueryParam("createExpedition") @DefaultValue("false") Boolean createExpedition) {
        String successMessage;
        ProcessController processController = (ProcessController) session.getAttribute("processController");

        // if no processController is found, we can't do anything
        if (processController == null) {
            return "{\"error\": \"No process was detected.\"}";
        }

        // check if user is logged in
        if (processController.getUserId() == null) {
            return "{\"error\": \"You must be logged in to upload.\"}";
        }

        // if the process controller was stored in the session, then the user wants to continue, set warning cleared
        processController.setClearedOfWarnings(true);
        processController.setValidated(true);

        // Create the process object --- this is done each time to orient the application
        Process p = new Process(
                uploadPath(),
                processController
        );

        String outputPrefix = processController.getExpeditionCode() + "_output";

        // create this expedition if the user wants to
        if (createExpedition) {
            processController.setExpeditionTitle(processController.getExpeditionCode() + " spreadsheet");
            p.runExpeditionCreate();
        }

        if (!processController.isExpeditionAssignedToUserAndExists()) {
            p.runExpeditionCheck();
        }

        if (processController.isExpeditionCreateRequired()) {
            // ask the user if they want to create this expedition
            return "{\"continue\": {\"message\": \"The expedition code \\\"" + JSONObject.escape(processController.getExpeditionCode()) +
                    "\\\" does not exist.  " +
                    "Do you wish to create it now?<br><br>" +
                    "If you choose to continue, your data will be associated with this new expedition code.\"}}";
        }

        String destination = uploadPath();

        TabularDataReader tdr = p.getTabularDataReader();

        // convert the dataset to csv file for uploading
        CsvTabularDataConverter csvTabularDataConverter = new CsvTabularDataConverter(
                tdr, destination, outputPrefix);

        tdr.closeFile();

        List<String> acceptableColumns = new LinkedList<>();
        for (Attribute attribute : processController.getMapping().getAllAttributes(processController.getMapping().getDefaultSheetName())) {
            acceptableColumns.add(attribute.getColumn());
        }
        csvTabularDataConverter.convert(acceptableColumns);

        Resolver resolver = new Resolver(processController.getExpeditionCode(), processController.getProjectId(),
                                        "Resource");
        // upload the dataset
        mySqlUploader.execute(resolver.getIdentifier(), acceptableColumns,csvTabularDataConverter.getCsvFile().getPath());

        // Detect if this is user=demo or not.  If this is "demo" then do not request EZIDs.
        // User account Demo can still create Data Groups, but they just don't get registered and will be purged periodically
        boolean ezidRequest = true;
        if (username.equals("demo") || sm.retrieveValue("ezidRequests").equalsIgnoreCase("false")) {
            ezidRequest = false;
        }

        // Mint the data group
        BcidMinter bcidMinter = new BcidMinter(ezidRequest);
        String identifier = bcidMinter.createEntityBcid(new Bcid(processController.getUserId(), "http://purl.org/dc/dcmitype/Dataset",
                processController.getExpeditionCode() + " Dataset", null, null, null,
                processController.getFinalCopy(), false));
        successMessage = "Dataset Identifier: http://n2t.net/" + identifier + " (wait 15 minutes for resolution to become active)";

        // Associate the expeditionCode with this identifier
        ExpeditionMinter expedition = new ExpeditionMinter();
        expedition.attachReferenceToExpedition(processController.getExpeditionCode(), identifier, processController.getProjectId());
        successMessage += "<br>\t" + "Data Elements Root: " + processController.getExpeditionCode();

        // delete the temporary file now that it has been uploaded
        new File(processController.getInputFilename()).delete();

        successMessage += "<br><font color=#188B00>Successfully Uploaded!</font><br><br>";
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


