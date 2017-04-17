package biocode.fims.arms.query;

import biocode.fims.arms.entities.Deployment;
import biocode.fims.digester.Attribute;
import biocode.fims.digester.Validation;
import biocode.fims.query.writers.QueryWriter;
import biocode.fims.run.TemplateProcessor;
import biocode.fims.settings.PathManager;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to write a list of {@link Deployment}s to different file formats
 */
public class DeploymentsWriter {
    private static final Logger logger = LoggerFactory.getLogger(DeploymentsWriter.class);

    private final String outputDirectory;
    private final ArrayList<Attribute> attributes;
    private final List<Deployment> deployments;
    private final QueryWriter queryWriter;

    /**
     *
     * @param deployments
     * @param outputDirectory
     * @param attributes the list of {@link Attribute} to be written
     * @param sheetName name of the sheet in the excel workbook where the data exists
     */
    public DeploymentsWriter(List<Deployment> deployments, String outputDirectory,
                             ArrayList<Attribute> attributes, String sheetName) {
        this.outputDirectory = outputDirectory;
        this.deployments = deployments;
        this.attributes = attributes;

        queryWriter = new QueryWriter(
                attributes,
                sheetName
        );

        addDataToQueryWriter();
    }

    /**
     * create a {@link Row} representing each {@link Deployment}
     */
    private void addDataToQueryWriter() {
        int currentRow = 0;
        for (Deployment d: deployments) {
            Row row = queryWriter.createRow(currentRow);
            mapDeploymentToRow(d, row);
            currentRow++;
        }
    }

    /**
     * create a {@link Row} with each property of {@link Deployment} in the list of attributes, written in a cell
     * @param deployment
     * @param row
     */
    private void mapDeploymentToRow(Deployment deployment, Row row) {
        for (Attribute a: attributes) {
            try {
                Method getter = new PropertyDescriptor(a.getColumn_internal(), Deployment.class).getReadMethod();
                Object value = getter.invoke(deployment);
                queryWriter.createCell(row, a.getColumn(), (value == null) ? "": String.valueOf(value));
            } catch (IntrospectionException e) {
                logger.error("Couldn't find getter for Deployment property " + a.getColumn_internal(), e);
            } catch (IllegalAccessException|InvocationTargetException e) {
                logger.error("Error invoking getter for Deployment property " + a.getColumn_internal(), e);
            }
        }
    }

    /**
     * write the list of {@link Deployment}s to an excel file
     * @return
     */
    public File writeExcel(int projectId) {
        String dataOutputPath = queryWriter.writeExcel(PathManager.createUniqueFile("output.xlsx", outputDirectory));

        // Here we attach the other components of the excel sheet found with
        XSSFWorkbook justData = null;
        try {
            justData = new XSSFWorkbook(new FileInputStream(dataOutputPath));
        } catch (IOException e) {
            logger.warn("Unable to open Workbook", e);
        }

        TemplateProcessor t = new TemplateProcessor(projectId, outputDirectory, justData);
        return new File(t.createExcelFileFromExistingSources("Samples", outputDirectory).getAbsolutePath());
    }

    /**
     * write the list of {@link Deployment}s to a tab delimited text file.
     * @return
     */
    public File writeTabDelimitedText() {
        return new File(queryWriter.writeTAB(PathManager.createUniqueFile("output.txt", outputDirectory), false));
    }
}
