package mysql;

import biocode.fims.fimsExceptions.ServerErrorException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Class to upload a csv file to a mysql database
 */
public class MySqlUploader {
    private String csvFilepath;
    private List<String> columnNames;

    public MySqlUploader(String csvFilepath, List<String> columnNames) {
        this.csvFilepath = csvFilepath;
        this.columnNames = columnNames;
    }

    public void execute(String identifier) {
        deleteExistingDataset(identifier);

        Connection conn = null;
        PreparedStatement stmt = null;
        StringBuilder sql = new StringBuilder();

        try {
            conn = MySqlDatasetDatabase.getConnection();
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
            MySqlDatasetDatabase.close(conn, stmt, null);
        }
    }

    private void deleteExistingDataset(String identifier) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = MySqlDatasetDatabase.getConnection();
            String sql = "DELETE FROM dataset WHERE identifier = ?";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, identifier);

            stmt.execute();
        } catch (SQLException e) {
            throw new ServerErrorException(e);
        } finally {
            MySqlDatasetDatabase.close(conn, stmt, null);
        }
    }
}
