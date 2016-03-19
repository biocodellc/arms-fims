package biocode.fims.mysql;

import biocode.fims.fimsExceptions.ServerErrorException;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.sql.DataSource;
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

    public DataSource getDataSource() {
        return dataSource;
    }

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public MySqlUploader() {
    }

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
//            dataSource.close(conn, stmt, null);
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
//            MySqlDatasetDatabase.close(conn, stmt, null);
        }
    }

    public static void main(String[] args) {
        ApplicationContext context =
//                new ClassPathXmlApplicationContext();
                new ClassPathXmlApplicationContext("applicationContext.xml");

//        new MysqlDataSource().
        MySqlUploader mySqlUploader = (MySqlUploader) context.getBean("mySqlUploader");
        int i = 0;
    }
}
