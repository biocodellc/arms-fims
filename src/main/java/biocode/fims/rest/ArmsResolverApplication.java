package biocode.fims.rest;

/**
 * Jersey Application for ARMS Resolver services
 */
public class ArmsResolverApplication extends FimsApplication {

    public ArmsResolverApplication() {
        super();
        packages("biocode.fims.rest.services.id");
    }
}
