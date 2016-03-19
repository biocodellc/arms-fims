package mysql;

import biocode.fims.bcid.Database;
import biocode.fims.digester.Attribute;
import biocode.fims.digester.Mapping;
import biocode.fims.fimsExceptions.ServerErrorException;
import biocode.fims.settings.SettingsManager;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.digester3.Digester;

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
public final class MySqlDatasetDatabase extends Database {

//    public MySqlDatasetDatabase() {
//        try {
//            InitialContext ctx = new InitialContext();
//            dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/dataset");
//        } catch (NamingException e) {
//            throw new ServerErrorException("Error connecting to FIMS dataset db");
//        }
//    }
//
//    public Connection get
    private final static BasicDataSource dataSource = new BasicDataSource();

    static {
        SettingsManager sm = SettingsManager.getInstance("arms-fims.props");
        dataSource.setUsername(sm.retrieveValue("datasetUser"));
        dataSource.setPassword(sm.retrieveValue("datasetPassword"));
        dataSource.setUrl(sm.retrieveValue("datasetUrl"));
        dataSource.setDriverClassName(sm.retrieveValue("datasetClass"));
    }

    private MySqlDatasetDatabase() {}

    public static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new ServerErrorException(e);
        }
    }

    /**
     * verify that the dataset table reflects the project config mapping
     * @param mapping Mapping object generated from the project config file
     * @return
     */
    public static boolean validate(Mapping mapping) {
        List<String> definedColumns = new ArrayList<>();
        List<Attribute> attributes = mapping.getAllAttributes(mapping.getDefaultSheetName());
        for (Attribute attribute: attributes) {
            definedColumns.add(attribute.getColumn());
        }

        // table should always have an identifier column to map the data to a bcid
        definedColumns.add("identifier");

        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<String> tableColumns = new ArrayList<>();
        try {
            String sql = "SELECT column_name FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = 'dataset'";
            stmt = conn.prepareStatement(sql);

            rs = stmt.executeQuery();

            while (rs.next()) {
                tableColumns.add(rs.getString("column_name"));
            }
        } catch (SQLException e) {
            throw new ServerErrorException(e);
        } finally {
            close(conn, stmt, rs);
        }

        return tableColumns.equals(definedColumns);

    }

    public static void main(String[] args) {
        Mapping mapping = new Mapping();
        mapping.addMappingRules(new Digester(), new File("/Users/rjewing/IdeaProjects/biscicol-fims/web/tripleOutput/config.1.xml"));
//        mapping.addMappingRules(new Digester(), new File("/Users/rjewing/IdeaProjects/nmnh-fims/web/docs/SIBOT.xml"));
//        List<String> a = new ArrayList<>();
//        List<String> b = new ArrayList<>();
//        for (int i=0; i < 10; i++) {
//            a.add(String.valueOf(i));
//            b.add(String.valueOf(i));
//        }

//        System.out.println(a.equals(b));

        System.out.println(MySqlDatasetDatabase.validate(mapping));
    }

}
