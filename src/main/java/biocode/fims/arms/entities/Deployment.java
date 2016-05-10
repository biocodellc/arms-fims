package biocode.fims.arms.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.net.URI;
import java.util.Date;

/**
 * Deployment Entity object
 */
@Entity
@Table(name = "deployments")
public class Deployment {

    // ARMS Details
    private String armsModel;
    private String attachmentMethod;
    private String deploymentId;
    private boolean hasScrubbieLayer;
    private boolean laminates;
    private String newOrReused;
    private int numLayers;
    private boolean weightsAttached;

    // ARMS Process
    private String dataEntryPersonInCharge;
    private String deploymentPersonInCharge;
    private int durationToProcessing;
    private String notes;
    private String processingPersonInCharge;
    private String recoveryPersonInCharge;
    private String removalProtocol;

    // Data
    private boolean intentToBarcode;
    private boolean intentToCollectOtherDatatypes;
    private boolean intentToMetabarcode;
    private boolean intentToPhotographPlates;
    private boolean intentToPhotographSpecimens;
    private int numReplicatesInSet;
    private URI photoURL;

    // Deployment Time
    private Date actualDeploymentDate;
    private Date actualDeploymentTimeOfDay;
    private Date actualRecoveryDate;
    private Date actualRecoveryTimeOfDay;
    private Date intendedDeploymentDate;
    private Date intendedRecoveryDate;
    private int intendedSoakTime;

    // Location
    private String continentOcean;
    private String country;
    private String county;
    private float latitude;
    private float longitude;
    private int depth;
    private int depthOfBottom;
    private int errorRadius;
    private String habitat;
    private String horizontalDatum;
    private String island;
    private String islandGroup;
    private String locality;
    private String region;
    private String stateProvince;
    private String stationId;
    private String substrateType;
    private URI permitInformationUrl;

    private ArmsExpedition armsExpedition;

}
