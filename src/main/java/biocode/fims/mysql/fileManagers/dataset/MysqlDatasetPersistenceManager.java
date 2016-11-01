package biocode.fims.mysql.fileManagers.dataset;

import biocode.fims.arms.entities.Deployment;
import biocode.fims.arms.services.DeploymentService;
import biocode.fims.digester.Attribute;
import biocode.fims.digester.Mapping;
import biocode.fims.entities.Expedition;
import biocode.fims.fileManagers.dataset.Dataset;
import biocode.fims.fileManagers.dataset.DatasetPersistenceManager;
import biocode.fims.reader.CsvDatasetConverter;
import biocode.fims.renderers.Message;
import biocode.fims.renderers.RowMessage;
import biocode.fims.run.ProcessController;
import biocode.fims.service.ExpeditionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * {@link DatasetPersistenceManager} for mysql
 */
public class MysqlDatasetPersistenceManager implements DatasetPersistenceManager {
    private final ExpeditionService expeditionService;
    private final DeploymentService deploymentService;

    private Dataset dataset;

    @Autowired
    public MysqlDatasetPersistenceManager(ExpeditionService expeditionService, DeploymentService deploymentService) {
        this.expeditionService = expeditionService;
        this.deploymentService = deploymentService;
    }

    @Override
    public void upload(ProcessController processController, Dataset dataset) {
        this.dataset = dataset;
        List<String> acceptableColumnsInternal = new LinkedList<>();
        List<Attribute> attributes = processController.getMapping().getAllAttributes(processController.getMapping().getDefaultSheetName());
        for (Attribute attribute : attributes) {
            acceptableColumnsInternal.add(attribute.getColumn_internal());
        }

        CsvDatasetConverter datasetConverter = new CsvDatasetConverter(dataset, processController.getOutputFolder(), processController.getExpeditionCode() + "_output");
        datasetConverter.convert(attributes);

        // TODO change to reference within deploymentService when we refactor the expeditions table to each fims instance
        Expedition expedition = expeditionService.getExpedition(processController.getExpeditionCode(), processController.getProjectId());

        // upload the dataset
        deploymentService.bulkUpload(
                expedition.getExpeditionId(),
                acceptableColumnsInternal,
                datasetConverter.getCsvFile().getPath()
        );
    }

    @Override
    public boolean validate(ProcessController processController) {
        Mapping mapping = processController.getMapping();
        List<String> definedColumns = new ArrayList<>();
        List<Attribute> attributes = mapping.getAllAttributes(mapping.getDefaultSheetName());
        for (Attribute attribute : attributes) {
            definedColumns.add(attribute.getColumn_internal());
        }

        // table should always have an identifier column to map the data to a bcid
        definedColumns.add("expeditionId");

        List<String> tableColumns = deploymentService.getTableColumns();

        boolean matches = true;
        for (String col : definedColumns) {
            if (!tableColumns.contains(col)) {
                processController.addMessage(processController.getMapping().getDefaultSheetName(),
                        new RowMessage("database and configuration file need to be synced", "System Error", Message.ERROR));
                matches = false;
            }
        }

        return matches;
    }

    @Override
    public String getWebAddress() {
        return null;
    }

    @Override
    public String getGraph() {
        return null;
    }

    @Override
    public Dataset getDataset(ProcessController processController) {
        if (dataset == null) {
            List<String> columnNames = processController.getMapping().getColumnNamesForWorksheet(processController.getMapping().getDefaultSheetName());

            JSONArray deployments = new JSONArray();

            Expedition expedition = expeditionService.getExpedition(
                    processController.getExpeditionCode(),
                    processController.getProjectId()
            );

            if (expedition != null) {
                List<Deployment> deploymentsList = deploymentService.findAll(expedition.getExpeditionId());

                ObjectMapper mapper = new ObjectMapper();
                for (Deployment d: deploymentsList) {
                    deployments.add(mapper.valueToTree(d));
                }
            }

            dataset = new Dataset(columnNames, deployments);
        }

        return dataset;
    }
}
