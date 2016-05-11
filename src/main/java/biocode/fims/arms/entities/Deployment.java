package biocode.fims.arms.entities;

import javax.persistence.*;
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

    private ArmsExpedition armsExpedition;

    @Column(nullable = false)
    public String getArmsModel() {
        return armsModel;
    }

    public void setArmsModel(String armsModel) {
        this.armsModel = armsModel;
    }

    public String getAttachmentMethod() {
        return attachmentMethod;
    }

    public void setAttachmentMethod(String attachmentMethod) {
        this.attachmentMethod = attachmentMethod;
    }

    @Id
    @Column(nullable = false)
    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    @Column(nullable = false)
    public boolean hasScrubbieLayer() {
        return hasScrubbieLayer;
    }

    public void setHasScrubbieLayer(boolean hasScrubbieLayer) {
        this.hasScrubbieLayer = hasScrubbieLayer;
    }

    @Column(nullable = false)
    public boolean hasLaminates() {
        return laminates;
    }

    public void setLaminates(boolean laminates) {
        this.laminates = laminates;
    }

    @Column(nullable = false)
    public String getNewOrReused() {
        return newOrReused;
    }

    public void setNewOrReused(String newOrReused) {
        this.newOrReused = newOrReused;
    }

    @Column(nullable = false)
    public int getNumLayers() {
        return numLayers;
    }

    public void setNumLayers(int numLayers) {
        this.numLayers = numLayers;
    }

    @Column(nullable = false)
    public boolean hasWeightsAttached() {
        return weightsAttached;
    }

    public void setWeightsAttached(boolean weightsAttached) {
        this.weightsAttached = weightsAttached;
    }

    public String getDataEntryPersonInCharge() {
        return dataEntryPersonInCharge;
    }

    public void setDataEntryPersonInCharge(String dataEntryPersonInCharge) {
        this.dataEntryPersonInCharge = dataEntryPersonInCharge;
    }

    public String getDeploymentPersonInCharge() {
        return deploymentPersonInCharge;
    }

    public void setDeploymentPersonInCharge(String deploymentPersonInCharge) {
        this.deploymentPersonInCharge = deploymentPersonInCharge;
    }

    public int getDurationToProcessing() {
        return durationToProcessing;
    }

    public void setDurationToProcessing(int durationToProcessing) {
        this.durationToProcessing = durationToProcessing;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getProcessingPersonInCharge() {
        return processingPersonInCharge;
    }

    public void setProcessingPersonInCharge(String processingPersonInCharge) {
        this.processingPersonInCharge = processingPersonInCharge;
    }

    public String getRecoveryPersonInCharge() {
        return recoveryPersonInCharge;
    }

    public void setRecoveryPersonInCharge(String recoveryPersonInCharge) {
        this.recoveryPersonInCharge = recoveryPersonInCharge;
    }

    public String getRemovalProtocol() {
        return removalProtocol;
    }

    public void setRemovalProtocol(String removalProtocol) {
        this.removalProtocol = removalProtocol;
    }

    @Column(nullable = false)
    public boolean hasIntentToBarcode() {
        return intentToBarcode;
    }

    public void setIntentToBarcode(boolean intentToBarcode) {
        this.intentToBarcode = intentToBarcode;
    }

    @Column(nullable = false)
    public boolean hasIntentToCollectOtherDatatypes() {
        return intentToCollectOtherDatatypes;
    }

    public void setIntentToCollectOtherDatatypes(boolean intentToCollectOtherDatatypes) {
        this.intentToCollectOtherDatatypes = intentToCollectOtherDatatypes;
    }

    @Column(nullable = false)
    public boolean hasIntentToMetabarcode() {
        return intentToMetabarcode;
    }

    public void setIntentToMetabarcode(boolean intentToMetabarcode) {
        this.intentToMetabarcode = intentToMetabarcode;
    }

    @Column(nullable = false)
    public boolean hasIntentToPhotographPlates() {
        return intentToPhotographPlates;
    }

    public void setIntentToPhotographPlates(boolean intentToPhotographPlates) {
        this.intentToPhotographPlates = intentToPhotographPlates;
    }

    @Column(nullable = false)
    public boolean hasIntentToPhotographSpecimens() {
        return intentToPhotographSpecimens;
    }

    public void setIntentToPhotographSpecimens(boolean intentToPhotographSpecimens) {
        this.intentToPhotographSpecimens = intentToPhotographSpecimens;
    }

    public int getNumReplicatesInSet() {
        return numReplicatesInSet;
    }

    public void setNumReplicatesInSet(int numReplicatesInSet) {
        this.numReplicatesInSet = numReplicatesInSet;
    }

    public URI getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(URI photoURL) {
        this.photoURL = photoURL;
    }

    @Column(nullable = false)
    public Date getActualDeploymentDate() {
        return actualDeploymentDate;
    }

    public void setActualDeploymentDate(Date actualDeploymentDate) {
        this.actualDeploymentDate = actualDeploymentDate;
    }

    public Date getActualDeploymentTimeOfDay() {
        return actualDeploymentTimeOfDay;
    }

    public void setActualDeploymentTimeOfDay(Date actualDeploymentTimeOfDay) {
        this.actualDeploymentTimeOfDay = actualDeploymentTimeOfDay;
    }

    @Column(nullable = false)
    public Date getActualRecoveryDate() {
        return actualRecoveryDate;
    }

    public void setActualRecoveryDate(Date actualRecoveryDate) {
        this.actualRecoveryDate = actualRecoveryDate;
    }

    public Date getActualRecoveryTimeOfDay() {
        return actualRecoveryTimeOfDay;
    }

    public void setActualRecoveryTimeOfDay(Date actualRecoveryTimeOfDay) {
        this.actualRecoveryTimeOfDay = actualRecoveryTimeOfDay;
    }

    @Column(nullable = false)
    public Date getIntendedDeploymentDate() {
        return intendedDeploymentDate;
    }

    public void setIntendedDeploymentDate(Date intendedDeploymentDate) {
        this.intendedDeploymentDate = intendedDeploymentDate;
    }

    @Column(nullable = false)
    public Date getIntendedRecoveryDate() {
        return intendedRecoveryDate;
    }

    public void setIntendedRecoveryDate(Date intendedRecoveryDate) {
        this.intendedRecoveryDate = intendedRecoveryDate;
    }

    @Column(nullable = false)
    public int getIntendedSoakTime() {
        return intendedSoakTime;
    }

    public void setIntendedSoakTime(int intendedSoakTime) {
        this.intendedSoakTime = intendedSoakTime;
    }

    @Column(nullable = false)
    public String getContinentOcean() {
        return continentOcean;
    }

    public void setContinentOcean(String continentOcean) {
        this.continentOcean = continentOcean;
    }

    @Column(nullable = false)
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    @Column(nullable = false)
    public float getLatitude() {
        return latitude;
    }

    @Column(nullable = false)
    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    @Column(nullable = false)
    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getDepthOfBottom() {
        return depthOfBottom;
    }

    public void setDepthOfBottom(int depthOfBottom) {
        this.depthOfBottom = depthOfBottom;
    }

    public int getErrorRadius() {
        return errorRadius;
    }

    public void setErrorRadius(int errorRadius) {
        this.errorRadius = errorRadius;
    }

    public String getHabitat() {
        return habitat;
    }

    public void setHabitat(String habitat) {
        this.habitat = habitat;
    }

    public String getHorizontalDatum() {
        return horizontalDatum;
    }

    public void setHorizontalDatum(String horizontalDatum) {
        this.horizontalDatum = horizontalDatum;
    }

    public String getIsland() {
        return island;
    }

    public void setIsland(String island) {
        this.island = island;
    }

    public String getIslandGroup() {
        return islandGroup;
    }

    public void setIslandGroup(String islandGroup) {
        this.islandGroup = islandGroup;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    @Column(nullable = false)
    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getSubstrateType() {
        return substrateType;
    }

    public void setSubstrateType(String substrateType) {
        this.substrateType = substrateType;
    }

    @ManyToOne(optional = false)
    public ArmsExpedition getArmsExpedition() {
        return armsExpedition;
    }

    public void setArmsExpedition(ArmsExpedition armsExpedition) {
        this.armsExpedition = armsExpedition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Deployment that = (Deployment) o;

        if (hasScrubbieLayer != that.hasScrubbieLayer) return false;
        if (laminates != that.laminates) return false;
        if (getNumLayers() != that.getNumLayers()) return false;
        if (weightsAttached != that.weightsAttached) return false;
        if (getDurationToProcessing() != that.getDurationToProcessing()) return false;
        if (intentToBarcode != that.intentToBarcode) return false;
        if (intentToCollectOtherDatatypes != that.intentToCollectOtherDatatypes) return false;
        if (intentToMetabarcode != that.intentToMetabarcode) return false;
        if (intentToPhotographPlates != that.intentToPhotographPlates) return false;
        if (intentToPhotographSpecimens != that.intentToPhotographSpecimens) return false;
        if (getNumReplicatesInSet() != that.getNumReplicatesInSet()) return false;
        if (getIntendedSoakTime() != that.getIntendedSoakTime()) return false;
        if (Float.compare(that.getLatitude(), getLatitude()) != 0) return false;
        if (Float.compare(that.getLongitude(), getLongitude()) != 0) return false;
        if (getDepth() != that.getDepth()) return false;
        if (getDepthOfBottom() != that.getDepthOfBottom()) return false;
        if (getErrorRadius() != that.getErrorRadius()) return false;
        if (getArmsModel() != null ? !getArmsModel().equals(that.getArmsModel()) : that.getArmsModel() != null)
            return false;
        if (getAttachmentMethod() != null ? !getAttachmentMethod().equals(that.getAttachmentMethod()) : that.getAttachmentMethod() != null)
            return false;
        if (getDeploymentId() != null ? !getDeploymentId().equals(that.getDeploymentId()) : that.getDeploymentId() != null)
            return false;
        if (getNewOrReused() != null ? !getNewOrReused().equals(that.getNewOrReused()) : that.getNewOrReused() != null)
            return false;
        if (getDataEntryPersonInCharge() != null ? !getDataEntryPersonInCharge().equals(that.getDataEntryPersonInCharge()) : that.getDataEntryPersonInCharge() != null)
            return false;
        if (getDeploymentPersonInCharge() != null ? !getDeploymentPersonInCharge().equals(that.getDeploymentPersonInCharge()) : that.getDeploymentPersonInCharge() != null)
            return false;
        if (getNotes() != null ? !getNotes().equals(that.getNotes()) : that.getNotes() != null) return false;
        if (getProcessingPersonInCharge() != null ? !getProcessingPersonInCharge().equals(that.getProcessingPersonInCharge()) : that.getProcessingPersonInCharge() != null)
            return false;
        if (getRecoveryPersonInCharge() != null ? !getRecoveryPersonInCharge().equals(that.getRecoveryPersonInCharge()) : that.getRecoveryPersonInCharge() != null)
            return false;
        if (getRemovalProtocol() != null ? !getRemovalProtocol().equals(that.getRemovalProtocol()) : that.getRemovalProtocol() != null)
            return false;
        if (getPhotoURL() != null ? !getPhotoURL().equals(that.getPhotoURL()) : that.getPhotoURL() != null)
            return false;
        if (getActualDeploymentDate() != null ? !getActualDeploymentDate().equals(that.getActualDeploymentDate()) : that.getActualDeploymentDate() != null)
            return false;
        if (getActualDeploymentTimeOfDay() != null ? !getActualDeploymentTimeOfDay().equals(that.getActualDeploymentTimeOfDay()) : that.getActualDeploymentTimeOfDay() != null)
            return false;
        if (getActualRecoveryDate() != null ? !getActualRecoveryDate().equals(that.getActualRecoveryDate()) : that.getActualRecoveryDate() != null)
            return false;
        if (getActualRecoveryTimeOfDay() != null ? !getActualRecoveryTimeOfDay().equals(that.getActualRecoveryTimeOfDay()) : that.getActualRecoveryTimeOfDay() != null)
            return false;
        if (getIntendedDeploymentDate() != null ? !getIntendedDeploymentDate().equals(that.getIntendedDeploymentDate()) : that.getIntendedDeploymentDate() != null)
            return false;
        if (getIntendedRecoveryDate() != null ? !getIntendedRecoveryDate().equals(that.getIntendedRecoveryDate()) : that.getIntendedRecoveryDate() != null)
            return false;
        if (getContinentOcean() != null ? !getContinentOcean().equals(that.getContinentOcean()) : that.getContinentOcean() != null)
            return false;
        if (getCountry() != null ? !getCountry().equals(that.getCountry()) : that.getCountry() != null) return false;
        if (getCounty() != null ? !getCounty().equals(that.getCounty()) : that.getCounty() != null) return false;
        if (getHabitat() != null ? !getHabitat().equals(that.getHabitat()) : that.getHabitat() != null) return false;
        if (getHorizontalDatum() != null ? !getHorizontalDatum().equals(that.getHorizontalDatum()) : that.getHorizontalDatum() != null)
            return false;
        if (getIsland() != null ? !getIsland().equals(that.getIsland()) : that.getIsland() != null) return false;
        if (getIslandGroup() != null ? !getIslandGroup().equals(that.getIslandGroup()) : that.getIslandGroup() != null)
            return false;
        if (getLocality() != null ? !getLocality().equals(that.getLocality()) : that.getLocality() != null)
            return false;
        if (getRegion() != null ? !getRegion().equals(that.getRegion()) : that.getRegion() != null) return false;
        if (getStateProvince() != null ? !getStateProvince().equals(that.getStateProvince()) : that.getStateProvince() != null)
            return false;
        if (!getStationId().equals(that.getStationId())) return false;
        if (getSubstrateType() != null ? !getSubstrateType().equals(that.getSubstrateType()) : that.getSubstrateType() != null)
            return false;
        return getArmsExpedition() != null ? getArmsExpedition().equals(that.getArmsExpedition()) : that.getArmsExpedition() == null;

    }

    @Override
    public int hashCode() {
        int result = getArmsModel() != null ? getArmsModel().hashCode() : 0;
        result = 31 * result + (getAttachmentMethod() != null ? getAttachmentMethod().hashCode() : 0);
        result = 31 * result + (getDeploymentId() != null ? getDeploymentId().hashCode() : 0);
        result = 31 * result + (hasScrubbieLayer ? 1 : 0);
        result = 31 * result + (laminates ? 1 : 0);
        result = 31 * result + (getNewOrReused() != null ? getNewOrReused().hashCode() : 0);
        result = 31 * result + getNumLayers();
        result = 31 * result + (weightsAttached ? 1 : 0);
        result = 31 * result + (getDataEntryPersonInCharge() != null ? getDataEntryPersonInCharge().hashCode() : 0);
        result = 31 * result + (getDeploymentPersonInCharge() != null ? getDeploymentPersonInCharge().hashCode() : 0);
        result = 31 * result + getDurationToProcessing();
        result = 31 * result + (getNotes() != null ? getNotes().hashCode() : 0);
        result = 31 * result + (getProcessingPersonInCharge() != null ? getProcessingPersonInCharge().hashCode() : 0);
        result = 31 * result + (getRecoveryPersonInCharge() != null ? getRecoveryPersonInCharge().hashCode() : 0);
        result = 31 * result + (getRemovalProtocol() != null ? getRemovalProtocol().hashCode() : 0);
        result = 31 * result + (intentToBarcode ? 1 : 0);
        result = 31 * result + (intentToCollectOtherDatatypes ? 1 : 0);
        result = 31 * result + (intentToMetabarcode ? 1 : 0);
        result = 31 * result + (intentToPhotographPlates ? 1 : 0);
        result = 31 * result + (intentToPhotographSpecimens ? 1 : 0);
        result = 31 * result + getNumReplicatesInSet();
        result = 31 * result + (getPhotoURL() != null ? getPhotoURL().hashCode() : 0);
        result = 31 * result + (getActualDeploymentDate() != null ? getActualDeploymentDate().hashCode() : 0);
        result = 31 * result + (getActualDeploymentTimeOfDay() != null ? getActualDeploymentTimeOfDay().hashCode() : 0);
        result = 31 * result + (getActualRecoveryDate() != null ? getActualRecoveryDate().hashCode() : 0);
        result = 31 * result + (getActualRecoveryTimeOfDay() != null ? getActualRecoveryTimeOfDay().hashCode() : 0);
        result = 31 * result + (getIntendedDeploymentDate() != null ? getIntendedDeploymentDate().hashCode() : 0);
        result = 31 * result + (getIntendedRecoveryDate() != null ? getIntendedRecoveryDate().hashCode() : 0);
        result = 31 * result + getIntendedSoakTime();
        result = 31 * result + (getContinentOcean() != null ? getContinentOcean().hashCode() : 0);
        result = 31 * result + (getCountry() != null ? getCountry().hashCode() : 0);
        result = 31 * result + (getCounty() != null ? getCounty().hashCode() : 0);
        result = 31 * result + (getLatitude() != +0.0f ? Float.floatToIntBits(getLatitude()) : 0);
        result = 31 * result + (getLongitude() != +0.0f ? Float.floatToIntBits(getLongitude()) : 0);
        result = 31 * result + getDepth();
        result = 31 * result + getDepthOfBottom();
        result = 31 * result + getErrorRadius();
        result = 31 * result + (getHabitat() != null ? getHabitat().hashCode() : 0);
        result = 31 * result + (getHorizontalDatum() != null ? getHorizontalDatum().hashCode() : 0);
        result = 31 * result + (getIsland() != null ? getIsland().hashCode() : 0);
        result = 31 * result + (getIslandGroup() != null ? getIslandGroup().hashCode() : 0);
        result = 31 * result + (getLocality() != null ? getLocality().hashCode() : 0);
        result = 31 * result + (getRegion() != null ? getRegion().hashCode() : 0);
        result = 31 * result + (getStateProvince() != null ? getStateProvince().hashCode() : 0);
        result = 31 * result + getStationId().hashCode();
        result = 31 * result + (getSubstrateType() != null ? getSubstrateType().hashCode() : 0);
        result = 31 * result + (getArmsExpedition() != null ? getArmsExpedition().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Deployment{" +
                "deploymentId='" + deploymentId + '\'' +
                ", armsModel='" + armsModel + '\'' +
                ", armsExpedition=" + armsExpedition +
                '}';
    }
}
