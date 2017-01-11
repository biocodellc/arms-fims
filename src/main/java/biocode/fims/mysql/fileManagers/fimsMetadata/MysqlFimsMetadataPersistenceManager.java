package biocode.fims.mysql.fileManagers.fimsMetadata;

import biocode.fims.arms.entities.Deployment;
import biocode.fims.arms.services.DeploymentService;
import biocode.fims.digester.Attribute;
import biocode.fims.digester.Mapping;
import biocode.fims.entities.Expedition;
import biocode.fims.fileManagers.fimsMetadata.AbstractFimsMetadataPersistenceManager;
import biocode.fims.fileManagers.fimsMetadata.FimsMetadataPersistenceManager;
import biocode.fims.reader.CsvJsonConverter;
import biocode.fims.renderers.Message;
import biocode.fims.renderers.RowMessage;
import biocode.fims.run.ProcessController;
import biocode.fims.service.ExpeditionService;
import biocode.fims.settings.SettingsManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * {@link FimsMetadataPersistenceManager} for mysql
 */
public class MysqlFimsMetadataPersistenceManager extends AbstractFimsMetadataPersistenceManager implements FimsMetadataPersistenceManager {
    private final ExpeditionService expeditionService;
    private final DeploymentService deploymentService;

    private JSONArray fimsMetadata;

    @Autowired
    public MysqlFimsMetadataPersistenceManager(ExpeditionService expeditionService, DeploymentService deploymentService,
                                               SettingsManager settingsManager) {
        super(settingsManager);
        this.expeditionService = expeditionService;
        this.deploymentService = deploymentService;
    }

    @Override
    public void upload(ProcessController processController, JSONArray fimsMetadata, String filename) {
        this.fimsMetadata = fimsMetadata;
        List<String> acceptableColumnsInternal = new LinkedList<>();
        List<Attribute> attributes = processController.getMapping().getAllAttributes(processController.getMapping().getDefaultSheetName());
        for (Attribute attribute : attributes) {
            acceptableColumnsInternal.add(attribute.getColumn_internal());
        }

        CsvJsonConverter datasetConverter = new CsvJsonConverter(fimsMetadata, processController.getOutputFolder(), processController.getExpeditionCode() + "_output");
        datasetConverter.convert(attributes);

        // TODO change to reference within deploymentService when we refactor the expeditions table to each fims instance
        Expedition expedition = expeditionService.getExpedition(processController.getExpeditionCode(), processController.getProjectId());

        // upload the fimsMetadata
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
    public JSONArray getDataset(ProcessController processController) {
        if (fimsMetadata == null) {
            JSONArray dataset = new JSONArray();

            Expedition expedition = expeditionService.getExpedition(
                    processController.getExpeditionCode(),
                    processController.getProjectId()
            );

            if (expedition != null) {
                List<Deployment> deploymentsList = deploymentService.findAll(expedition.getExpeditionId());

                ObjectMapper mapper = new ObjectMapper();
                for (Deployment d: deploymentsList) {
                    dataset.add(mapper.valueToTree(d));
                }
            }

            fimsMetadata = dataset;
        }

        return fimsMetadata;
    }
}
