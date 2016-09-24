package biocode.fims;

import biocode.fims.digester.Attribute;
import biocode.fims.digester.Mapping;
import biocode.fims.settings.*;
import biocode.fims.utils.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.sql.*;
import java.sql.Connection;

/**
 * class to assist in arms testing
 */
public class DbBuilder {
    protected Connection conn;

    private final SettingsManager settingsManager;

    @Autowired
    public DbBuilder(SettingsManager settingsManager) throws Exception {
        this.settingsManager = settingsManager;
        String url = settingsManager.retrieveValue("datasetUrl");
        String user = settingsManager.retrieveValue("datasetUser");
        String pass = settingsManager.retrieveValue("datasetPassword");
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
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/applicationContext.xml");
        DbBuilder dbBuilder = applicationContext.getBean(DbBuilder.class);
        String projectConfig = "/Users/rjewing/IdeaProjects/biscicol-fims/src.main.web/tripleOutput/config.1.xml";
        dbBuilder.buildTable(projectConfig);

    }
}
