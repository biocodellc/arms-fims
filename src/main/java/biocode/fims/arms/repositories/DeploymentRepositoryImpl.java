package biocode.fims.arms.repositories;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Transactional("armsTransactionManager")
public class DeploymentRepositoryImpl implements DeploymentBulkUploadRepository {
    @PersistenceContext(unitName = "armsEntityManagerFactory")
    private EntityManager em;

    @Override
    @Transactional("armsTransactionManager")
    public void bulkUpload(int expeditionId, List<String> columnNames, String csvFilepath) {
        em.createNativeQuery("DELETE FROM deployments WHERE expeditionId = " + expeditionId).executeUpdate();

        StringBuilder sql = new StringBuilder();

        sql.append("LOAD DATA LOCAL INFILE '");
        sql.append(csvFilepath);
        sql.append("' INTO TABLE deployments FIELDS TERMINATED BY ',' ENCLOSED BY '\"' LINES TERMINATED BY '\\n' (");
        int col = 0;
        StringBuilder setColumnStatements = new StringBuilder();
        for (String colname : columnNames) {
            if (col > 0)
                sql.append(", ");
            // use variable to convert '' to NULL
            sql.append("@v" + colname + "");
            // build the set statements
            setColumnStatements.append(colname);
            setColumnStatements.append(" = NULLIF(@v");
            setColumnStatements.append(colname);
            setColumnStatements.append(", ''),");
            col++;
        }
        sql.append(") ");
        sql.append("SET ");
        sql.append(setColumnStatements);
        sql.append("expeditionId=");
        sql.append(expeditionId);

        em.createNativeQuery(sql.toString()).executeUpdate();

    }
}
