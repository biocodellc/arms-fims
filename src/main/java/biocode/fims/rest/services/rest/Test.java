package biocode.fims.rest.services.rest;

import biocode.fims.mysql.MySqlUploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Created by rjewing on 3/18/16.
 */
@Controller
@Path("test")
public class Test {

    @Autowired
    private MySqlUploader uploader;

    @GET
    public int test() throws Exception {
//        MySqlUploader uploader = new MySqlUploader();
//        return uploader.count();
        return 2;
    }
}
