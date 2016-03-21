package biocode.fims.rest;

import org.glassfish.jersey.server.mvc.jsp.JspMvcFeature;

/**
 * Created by rjewing on 3/18/16.
 */
public class ArmsResolverApplication extends FimsApplication {

    public ArmsResolverApplication() {
        super();
        packages("biocode.fims.rest.services.id");
        register(JspMvcFeature.class);
    }
}
