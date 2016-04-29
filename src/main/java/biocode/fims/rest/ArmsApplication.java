package biocode.fims.rest;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

/**
 * Jersey Application for arms-fims rest services
 */
public class ArmsApplication extends FimsApplication {

    public ArmsApplication() {
        super();
        packages("biocode.fims.rest.services.rest");
        register(MultiPartFeature.class);
    }
}
