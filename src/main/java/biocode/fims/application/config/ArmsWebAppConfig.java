package biocode.fims.application.config;

import biocode.fims.fileManagers.AuxilaryFileManager;
import biocode.fims.rest.services.rest.ValidateController;
import biocode.fims.service.OAuthProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.List;

/**
 * configuration class for Arms-fims webapp
 */
@Configuration
@Import({ArmsAppConfig.class, FimsWebAppConfig.class})
public class ArmsWebAppConfig {

    @Autowired
    private ArmsAppConfig armsAppConfig;
    @Autowired
    private OAuthProviderService providerService;

    @Bean
    @Scope("prototype")
    public List<AuxilaryFileManager> fileManagers() {
        return new ArrayList<>();
    }

    @Bean
    @Scope("prototype")
    public ValidateController validate() throws Exception {
        return new ValidateController(armsAppConfig.fimsAppConfig.expeditionService, armsAppConfig.FimsMetadataFileManager(),
               fileManagers(), providerService, armsAppConfig.fimsAppConfig.settingsManager);
    }
}
