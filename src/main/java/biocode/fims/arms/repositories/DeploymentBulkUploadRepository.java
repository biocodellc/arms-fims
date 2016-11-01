package biocode.fims.arms.repositories;

import java.util.List;

public interface DeploymentBulkUploadRepository {
    /**
     * bulk upload csv file of deployments to the Deployment table
     * @param expeditionId
     * @param columnNames
     * @param csvFilepath
     */
    void bulkUpload(int expeditionId, List<String> columnNames, String csvFilepath);
}
