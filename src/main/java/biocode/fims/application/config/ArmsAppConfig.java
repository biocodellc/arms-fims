package biocode.fims.application.config;

import biocode.fims.arms.services.DeploymentService;
import biocode.fims.fileManagers.fimsMetadata.FimsMetadataFileManager;
import biocode.fims.fileManagers.fimsMetadata.FimsMetadataPersistenceManager;
import biocode.fims.mysql.fileManagers.fimsMetadata.MysqlFimsMetadataPersistenceManager;
import biocode.fims.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;

/**
 * Configuration class for Arms-Fims applications. Including cli and webapps
 */
@Configuration
@ComponentScan(basePackages = {"biocode.fims.arms.services"})
@Import({FimsAppConfig.class})
@ImportResource({
        "classpath:arms-data-access-config.xml",
})
// declaring this here allows us to override any properties that are also included in arms-fims.props
@PropertySource(value = "classpath:biocode-fims.props", ignoreResourceNotFound = true)
@PropertySource("classpath:arms-fims.props")
public class ArmsAppConfig {
    @Autowired
    FimsAppConfig fimsAppConfig;
    @Autowired
    DeploymentService deploymentService;
    @Autowired
    MessageSource messageSource;

    @Bean
    @Scope("prototype")
    public FimsMetadataFileManager FimsMetadataFileManager() {
        FimsMetadataPersistenceManager persistenceManager = new MysqlFimsMetadataPersistenceManager(
                fimsAppConfig.expeditionService,
                deploymentService,
                armsProperties());
        return new FimsMetadataFileManager(persistenceManager, armsProperties(),
                fimsAppConfig.expeditionService, fimsAppConfig.bcidService, messageSource);
    }

    @Bean
    @Primary
    public ArmsProperties armsProperties() {
        return new ArmsProperties(fimsAppConfig.env);
    }
}
