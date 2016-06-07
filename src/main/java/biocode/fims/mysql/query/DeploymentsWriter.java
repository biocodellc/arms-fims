package biocode.fims.mysql.query;

import biocode.fims.arms.entities.Deployment;
import biocode.fims.config.ConfigurationFileFetcher;
import biocode.fims.digester.Attribute;
import biocode.fims.digester.Mapping;
import biocode.fims.digester.Validation;
import biocode.fims.query.QueryWriter;
import biocode.fims.run.TemplateProcessor;
import biocode.fims.settings.PathManager;
import org.apache.commons.digester3.Digester;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Class to write a list of {@link Deployment}s to different file formats
 */
public class DeploymentsWriter {
    private static final Logger logger = LoggerFactory.getLogger(DeploymentsWriter.class);

    private final List<Deployment> deployments;
    private final String outputDirectory;
    private final int projectId;
    private final QueryWriter queryWriter;

    public DeploymentsWriter(List<Deployment> deployments, String outputDirectory, int projectId) {
        this.deployments = deployments;
        this.outputDirectory = outputDirectory;
        this.projectId = projectId;

        File configFile = new ConfigurationFileFetcher(projectId, outputDirectory, false).getOutputFile();

        Mapping mapping = new Mapping();
        mapping.addMappingRules(new Digester(), configFile);

        Validation validation = new Validation();
        validation.addValidationRules(new Digester(), configFile, mapping);

        String defaultSheetName = mapping.getDefaultSheetName();

        queryWriter = new QueryWriter(
                mapping.getAllAttributes(defaultSheetName),
                defaultSheetName,
                validation
        );

        addDataToQueryWriter(deployments, mapping.getAllAttributes(defaultSheetName));
    }

    /**
     * create a {@link Row} representing each {@link Deployment}
     * @param deployments
     * @param attributes
     */
    private void addDataToQueryWriter(List<Deployment> deployments, List<Attribute> attributes) {
        int currentRow = 0;
        for (Deployment d: deployments) {
            Row row = queryWriter.createRow(currentRow);
            mapDeploymentToRow(d, row, attributes);
            currentRow++;
        }
    }

    /**
     * create a {@link Row} with each property of {@link Deployment} written in a cell
     * @param deployment
     * @param row
     * @param attributes
     */
    private void mapDeploymentToRow(Deployment deployment, Row row, List<Attribute> attributes) {
        queryWriter.createCell(row, getColumn("armsModel", attributes), deployment.getArmsModel());
        queryWriter.createCell(row, getColumn("attachmentMethod", attributes), deployment.getAttachmentMethod());
        queryWriter.createCell(row, getColumn("deploymentId", attributes), deployment.getDeploymentId());
        queryWriter.createCell(row, getColumn("hasScrubbieLayer", attributes), deployment.hasScrubbieLayer());
        queryWriter.createCell(row, getColumn("laminates", attributes), deployment.hasLaminates());
        queryWriter.createCell(row, getColumn("newOrReused", attributes), deployment.getNewOrReused());
        queryWriter.createCell(row, getColumn("numLayers", attributes), String.valueOf(deployment.getNumLayers()));
        queryWriter.createCell(row, getColumn("weightsAttached", attributes), deployment.hasWeightsAttached());
        queryWriter.createCell(row, getColumn("dataEntryPersonInCharge", attributes), deployment.getDataEntryPersonInCharge());
        queryWriter.createCell(row, getColumn("deploymentPersonInCharge", attributes), deployment.getDeploymentPersonInCharge());
        queryWriter.createCell(row, getColumn("durationToProcessing", attributes), String.valueOf(deployment.getDurationToProcessing()));
        queryWriter.createCell(row, getColumn("notes", attributes), deployment.getNotes());
        queryWriter.createCell(row, getColumn("processingPersonInCharge", attributes), deployment.getProcessingPersonInCharge());
        queryWriter.createCell(row, getColumn("recoveryPersonInCharge", attributes), deployment.getRecoveryPersonInCharge());
        queryWriter.createCell(row, getColumn("removalProtocol", attributes), deployment.getRemovalProtocol());
        queryWriter.createCell(row, getColumn("intentToBarcode", attributes), deployment.hasIntentToBarcode());
        queryWriter.createCell(row, getColumn("intentToCollectOtherDatatypes", attributes), deployment.hasIntentToCollectOtherDatatypes());
        queryWriter.createCell(row, getColumn("intentToMetabarcode", attributes), deployment.hasIntentToMetabarcode());
        queryWriter.createCell(row, getColumn("intentToPhotographPlates", attributes), deployment.hasIntentToPhotographPlates());
        queryWriter.createCell(row, getColumn("intentToPhotographSpecimens", attributes), deployment.hasIntentToPhotographSpecimens());
        queryWriter.createCell(row, getColumn("numReplicatesInSet", attributes), String.valueOf(deployment.getNumReplicatesInSet()));
        queryWriter.createCell(row, getColumn("photoUrl", attributes), String.valueOf(deployment.getPhotoUrl()));
        queryWriter.createCell(row, getColumn("actualDeploymentDate", attributes), String.valueOf(deployment.getActualDeploymentDate()));
        queryWriter.createCell(row, getColumn("actualDeploymentTimeOfDay", attributes), String.valueOf(deployment.getActualDeploymentTimeOfDay()));
        queryWriter.createCell(row, getColumn("actualRecoveryDate", attributes), String.valueOf(deployment.getActualRecoveryDate()));
        queryWriter.createCell(row, getColumn("actualRecoveryTimeOfDay", attributes), String.valueOf(deployment.getActualRecoveryTimeOfDay()));
        queryWriter.createCell(row, getColumn("intendedDeploymentDate", attributes), String.valueOf(deployment.getIntendedDeploymentDate()));
        queryWriter.createCell(row, getColumn("intendedRecoveryDate", attributes), String.valueOf(deployment.getIntendedRecoveryDate()));
        queryWriter.createCell(row, getColumn("intendedSoakTimeInYears", attributes), String.valueOf(deployment.getIntendedSoakTimeInYears()));
        queryWriter.createCell(row, getColumn("continentOcean", attributes), deployment.getContinentOcean());
        queryWriter.createCell(row, getColumn("country", attributes), deployment.getCountry());
        queryWriter.createCell(row, getColumn("county", attributes), deployment.getCounty());
        queryWriter.createCell(row, getColumn("decimalLatitude", attributes), String.valueOf(deployment.getDecimalLatitude()));
        queryWriter.createCell(row, getColumn("decimalLongitude", attributes), String.valueOf(deployment.getDecimalLongitude()));
        queryWriter.createCell(row, getColumn("depthInMeters", attributes), String.valueOf(deployment.getDepthInMeters()));
        queryWriter.createCell(row, getColumn("depthOfBottomMeters", attributes), String.valueOf(deployment.getDepthOfBottomMeters()));
        queryWriter.createCell(row, getColumn("errorRadius", attributes), String.valueOf(deployment.getErrorRadius()));
        queryWriter.createCell(row, getColumn("habitat", attributes), deployment.getHabitat());
        queryWriter.createCell(row, getColumn("horizontalDatum", attributes), deployment.getHorizontalDatum());
        queryWriter.createCell(row, getColumn("island", attributes), deployment.getIsland());
        queryWriter.createCell(row, getColumn("islandGroup", attributes), deployment.getIslandGroup());
        queryWriter.createCell(row, getColumn("locality", attributes), deployment.getLocality());
        queryWriter.createCell(row, getColumn("region", attributes), deployment.getRegion());
        queryWriter.createCell(row, getColumn("stateProvince", attributes), deployment.getStateProvince());
        queryWriter.createCell(row, getColumn("stationId", attributes), deployment.getStationId());
        queryWriter.createCell(row, getColumn("substrateType", attributes), deployment.getSubstrateType());
    }

    private String getColumn(String columnInternal, List<Attribute> attributes) {
        for (Attribute a: attributes) {
            if (a.getColumn_internal().equals(columnInternal)) {
                return a.getColumn();
            }
        }
        return "Incorrect Mapping, contact system administer";
    }

    /**
     * write the list of {@link Deployment}s to an excel file
     * @return
     */
    public String writeExcel() {
        String dataOutputPath = queryWriter.writeExcel(PathManager.createUniqueFile("output.xlsx", outputDirectory));

        // Here we attach the other components of the excel sheet found with
        XSSFWorkbook justData = null;
        try {
            justData = new XSSFWorkbook(new FileInputStream(dataOutputPath));
        } catch (IOException e) {
            logger.warn("Unable to open Workbook", e);
        }

        TemplateProcessor t = new TemplateProcessor(projectId, outputDirectory, false, justData);
        return t.createExcelFileFromExistingSources("Samples", outputDirectory).getAbsolutePath();
    }
}
