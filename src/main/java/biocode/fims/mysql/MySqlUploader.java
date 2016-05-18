package biocode.fims.mysql;

import biocode.fims.arms.services.DeploymentService;
import biocode.fims.bcid.Database;
import biocode.fims.fimsExceptions.ServerErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Class to upload a csv file to a mysql database
 */
@Repository
public class MySqlUploader {

    private final DataSource dataSource;
    private final DeploymentService deploymentService;

    @Autowired
    public MySqlUploader(DataSource dataSource, DeploymentService deploymentService) {
        this.dataSource = dataSource;
        this.deploymentService = deploymentService;
    }

    @Transactional
    public void execute(int expeditionId, List<String> columnNames, String csvFilepath) {
        deploymentService.deleteAll(expeditionId);

        Connection conn = null;
        PreparedStatement stmt = null;
        StringBuilder sql = new StringBuilder();

        try {
            conn = dataSource.getConnection();
            sql.append("LOAD DATA LOCAL INFILE ? INTO TABLE deployments FIELDS TERMINATED BY ',' LINES TERMINATED BY '\\n' (");
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
            sql.append("expeditionId=?");

            stmt = conn.prepareStatement(sql.toString());
            stmt.setString(1, csvFilepath);
            stmt.setInt(2, expeditionId);

            stmt.execute();
        } catch (SQLException e) {
            throw new ServerErrorException(e);
        } finally {
            Database.close(conn, stmt, null);
        }
    }
}
