package biocode.fims.mysql;

import biocode.fims.bcid.Database;
import biocode.fims.digester.Attribute;
import biocode.fims.digester.Mapping;
import biocode.fims.fimsExceptions.ServerErrorException;
import org.apache.commons.digester3.Digester;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * helper class providing access to the mysql dataset database
 */
@Repository
public final class MySqlDatasetTableValidator {

    private final DataSource dataSource;

    @Autowired
    public MySqlDatasetTableValidator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * verify that the dataset table reflects the project config mapping
     * @param mapping Mapping object generated from the project config file
     * @return
     */
    public boolean validate(Mapping mapping) {
        List<String> definedColumns = new ArrayList<>();
        List<Attribute> attributes = mapping.getAllAttributes(mapping.getDefaultSheetName());
        for (Attribute attribute: attributes) {
            definedColumns.add(attribute.getColumn());
        }

        // table should always have an identifier column to map the data to a bcid
        definedColumns.add("identifier");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<String> tableColumns = new ArrayList<>();
        try {
            String sql = "SELECT column_name FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = 'dataset'";
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(sql);

            rs = stmt.executeQuery();

            while (rs.next()) {
                tableColumns.add(rs.getString("column_name"));
            }
        } catch (SQLException e) {
            throw new ServerErrorException(e);
        } finally {
            Database.close(conn, stmt, rs);
        }

        return tableColumns.equals(definedColumns);

    }

}
