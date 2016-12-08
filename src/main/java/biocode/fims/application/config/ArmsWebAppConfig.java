package biocode.fims.application.config;

import biocode.fims.fileManagers.AuxilaryFileManager;
import biocode.fims.rest.services.rest.Validate;
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
    public Validate validate() throws Exception {
        return new Validate(armsAppConfig.fimsAppConfig.expeditionService, armsAppConfig.FimsMetadataFileManager(),
               fileManagers(), providerService, armsAppConfig.fimsAppConfig.settingsManager);
    }
}
