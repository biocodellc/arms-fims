package biocode.fims.application.config;

import org.springframework.core.env.Environment;

/**
 * @author rjewing
 */
public class ArmsProperties extends FimsProperties {
    public ArmsProperties(Environment env) {
        super(env);
    }

    public String datasetUrl() {
        return env.getRequiredProperty("datasetUrl");
    }

    public String datasetUser() {
        return env.getRequiredProperty("datasetUser");
    }

    public String datasetPassword() {
        return env.getRequiredProperty("datasetPassword");
    }

    public int projectId() {
        return env.getRequiredProperty("projectId", int.class);
    }

    public String expeditionResolverTarget() {
        return env.getRequiredProperty("bcid.resolverTargets.expedition");
    }
}
