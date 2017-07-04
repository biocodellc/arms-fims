package biocode.fims;

import biocode.fims.application.config.ArmsAppConfig;
import biocode.fims.application.config.ArmsProperties;
import biocode.fims.digester.Attribute;
import biocode.fims.digester.Mapping;
import biocode.fims.utils.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;
import java.sql.*;
import java.sql.Connection;

/**
 * class to assist in arms testing
 */
public class DbBuilder {
    protected Connection conn;

    private final ArmsProperties props;

    @Autowired
    public DbBuilder(ArmsProperties props) throws Exception {
        this.props = props;
        String url = props.datasetUrl();
        String user = props.datasetUser();
        String pass = props.datasetPassword();
        conn = DriverManager.getConnection(url, user, pass);
    }

    public void buildTable(String projectConfig) throws Exception {
        Mapping mapping = new Mapping();
        mapping.addMappingRules(new File(projectConfig));

        Statement stmt = conn.createStatement();
        stmt.addBatch("DROP TABLE IF EXISTS `dataset`;");
        stmt.addBatch("CREATE TABLE `dataset` (`id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT, KEY (`id`));");
        for (Attribute attribute: mapping.getAllAttributes(mapping.getDefaultSheetName())) {
            stmt.addBatch("ALTER TABLE `dataset` ADD COLUMN `" + attribute.getColumn() + "` VARCHAR(255);\n");
        }
        stmt.addBatch("ALTER TABLE `dataset` ADD COLUMN `identifier` VARCHAR(100);\n");

        stmt.executeBatch();

    }


    public void queryMysql() throws Exception {
        Timer t = new Timer();
        Statement stmt = conn.createStatement();
        stmt.executeQuery("Select * from dataset");
        t.lap("select *");

        t = new Timer();
        stmt.executeQuery("Select * from dataset where id <= 5000 AND id >= 4000");
        t.lap("query id");
    }

    public static void main(String args[]) throws Exception {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(ArmsAppConfig.class);
        DbBuilder dbBuilder = applicationContext.getBean(DbBuilder.class);
        String projectConfig = "/Users/rjewing/IdeaProjects/biscicol-fims/src.main.web/tripleOutput/config.1.xml";
        dbBuilder.buildTable(projectConfig);

    }
}
