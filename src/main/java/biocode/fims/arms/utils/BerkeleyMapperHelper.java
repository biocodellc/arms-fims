package biocode.fims.arms.utils;

import biocode.fims.digester.Attribute;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to retrieve a list of {@link Attribute} nedded for the BerkeleyMapper tabfile
 */
public class BerkeleyMapperHelper {
    private static final List<String> columns = new ArrayList<String>() {{
        add("deploymentId");
        add("decimalLatitude");
        add("decimalLongitude");
        add("substrateType");
        add("depthInMeters");
        add("actualRecoveryDate");
    }};

    public static ArrayList<Attribute> getAttributes(ArrayList<Attribute> allAttributes) {
        ArrayList<Attribute> berkeleyMapperAttributes = new ArrayList<>();
        for (Attribute a: allAttributes) {
            if (columns.contains(a.getColumn_internal())) {
                berkeleyMapperAttributes.add(a);
            }
        }
        return berkeleyMapperAttributes;
    }
}
