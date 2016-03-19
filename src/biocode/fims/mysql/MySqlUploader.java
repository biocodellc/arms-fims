package biocode.fims.mysql;

import biocode.fims.bcid.Database;
import biocode.fims.fimsExceptions.ServerErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
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

    @Autowired
    public MySqlUploader(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void execute(String identifier, List<String> columnNames, String csvFilepath) {
        deleteExistingDataset(identifier);

        Connection conn = null;
        PreparedStatement stmt = null;
        StringBuilder sql = new StringBuilder();

        try {
            conn = dataSource.getConnection();
            sql.append("LOAD DATA INFILE ? INTO TABLE dataset FIELDS TERMINATED BY ',' LINES TERMINATED BY '\\n' (");
            int col = 0;
            for (String colname : columnNames) {
                if (col > 0)
                    sql.append(", ");
                sql.append("`" + colname + "`");
                col++;
            }
            sql.append(") ");
            sql.append("set identifier=?");

            stmt = conn.prepareStatement(sql.toString());
            stmt.setString(1, csvFilepath);
            stmt.setString(2, identifier);

            stmt.execute();
        } catch (SQLException e) {
            throw new ServerErrorException(e);
        } finally {
            Database.close(conn, stmt, null);
        }
    }

    private void deleteExistingDataset(String identifier) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dataSource.getConnection();
            String sql = "DELETE FROM dataset WHERE identifier = ?";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, identifier);

            stmt.execute();
        } catch (SQLException e) {
            throw new ServerErrorException(e);
        } finally {
            Database.close(conn, stmt, null);
        }
    }
}
