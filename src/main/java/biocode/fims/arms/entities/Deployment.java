package biocode.fims.arms.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.net.URI;
import java.util.Date;

/**
 * Deployment Entity object
 */
@Entity
@Table(name = "deployments")
public class Deployment {
    private int id;

    // ARMS Details
    private String armsModel;
    private String attachmentMethod;
    private String deploymentId;
    private String hasScrubbieLayer;
    private String laminates;
    private String newOrReused;
    private int numLayers;
    private String weightsAttached;

    // ARMS Process
    private String dataEntryPersonInCharge;
    private String deploymentPersonInCharge;
    private Integer durationToProcessing;
    private String notes;
    private String processingPersonInCharge;
    private String recoveryPersonInCharge;
    private String removalProtocol;

    // Data
    private String intentToBarcode;
    private String intentToCollectOtherDatatypes;
    private String intentToMetabarcode;
    private String intentToPhotographPlates;
    private String intentToPhotographSpecimens;
    private Integer numReplicatesInSet;
    private URI photoUrl;

    // Deployment Time
    private String actualDeploymentDate;
    private String actualDeploymentTimeOfDay;
    private String actualRecoveryDate;
    private String actualRecoveryTimeOfDay;
    private String intendedDeploymentDate;
    private String intendedRecoveryDate;
    private int intendedSoakTimeInYears;

    // Location
    private String continentOcean;
    private String country;
    private String county;
    private float decimalLatitude;
    private float decimalLongitude;
    private int depthInMeters;
    private Integer depthOfBottomMeters;
    private Integer errorRadius;
    private String habitat;
    private String horizontalDatum;
    private String island;
    private String islandGroup;
    private String locality;
    private String region;
    private String stateProvince;
    private String stationId;
    private String substrateType;
    private String replicateLabel;
    private String siteDetails;

    private ArmsExpedition armsExpedition;

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public String getHasScrubbieLayer() {
        return hasScrubbieLayer;
    }

    public void setHasScrubbieLayer(String hasScrubbieLayer) {
        this.hasScrubbieLayer = hasScrubbieLayer;
    }

    public String getLaminates() {
        return laminates;
    }

    public void setLaminates(String laminates) {
        this.laminates = laminates;
    }

    public String getNewOrReused() {
        return newOrReused;
    }

    public void setNewOrReused(String newOrReused) {
        this.newOrReused = newOrReused;
    }

    public int getNumLayers() {
        return numLayers;
    }

    public void setNumLayers(int numLayers) {
        this.numLayers = numLayers;
    }

    public String getWeightsAttached() {
        return weightsAttached;
    }

    public void setWeightsAttached(String weightsAttached) {
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

    public Integer getDurationToProcessing() {
        return durationToProcessing;
    }

    public void setDurationToProcessing(Integer durationToProcessing) {
        this.durationToProcessing = durationToProcessing;
    }

    @Column(columnDefinition = "text null")
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

    public String getIntentToBarcode() {
        return intentToBarcode;
    }

    public void setIntentToBarcode(String intentToBarcode) {
        this.intentToBarcode = intentToBarcode;
    }

    public String getIntentToCollectOtherDatatypes() {
        return intentToCollectOtherDatatypes;
    }

    public void setIntentToCollectOtherDatatypes(String intentToCollectOtherDatatypes) {
        this.intentToCollectOtherDatatypes = intentToCollectOtherDatatypes;
    }

    public String getIntentToMetabarcode() {
        return intentToMetabarcode;
    }

    public void setIntentToMetabarcode(String intentToMetabarcode) {
        this.intentToMetabarcode = intentToMetabarcode;
    }

    public String getIntentToPhotographPlates() {
        return intentToPhotographPlates;
    }

    public void setIntentToPhotographPlates(String intentToPhotographPlates) {
        this.intentToPhotographPlates = intentToPhotographPlates;
    }

    public String getIntentToPhotographSpecimens() {
        return intentToPhotographSpecimens;
    }

    public void setIntentToPhotographSpecimens(String intentToPhotographSpecimens) {
        this.intentToPhotographSpecimens = intentToPhotographSpecimens;
    }

    public Integer getNumReplicatesInSet() {
        return numReplicatesInSet;
    }

    public void setNumReplicatesInSet(Integer numReplicatesInSet) {
        this.numReplicatesInSet = numReplicatesInSet;
    }

    @Column(columnDefinition = "varchar(2083)")
    public URI getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(URI photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getActualDeploymentDate() {
        return actualDeploymentDate;
    }

    public void setActualDeploymentDate(String actualDeploymentDate) {
        this.actualDeploymentDate = actualDeploymentDate;
    }

    public String getActualDeploymentTimeOfDay() {
        return actualDeploymentTimeOfDay;
    }

    public void setActualDeploymentTimeOfDay(String actualDeploymentTimeOfDay) {
        this.actualDeploymentTimeOfDay = actualDeploymentTimeOfDay;
    }

    public String getActualRecoveryDate() {
        return actualRecoveryDate;
    }

    public void setActualRecoveryDate(String actualRecoveryDate) {
        this.actualRecoveryDate = actualRecoveryDate;
    }

    public String getActualRecoveryTimeOfDay() {
        return actualRecoveryTimeOfDay;
    }

    public void setActualRecoveryTimeOfDay(String actualRecoveryTimeOfDay) {
        this.actualRecoveryTimeOfDay = actualRecoveryTimeOfDay;
    }

    public String getIntendedDeploymentDate() {
        return intendedDeploymentDate;
    }

    public void setIntendedDeploymentDate(String intendedDeploymentDate) {
        this.intendedDeploymentDate = intendedDeploymentDate;
    }

    public String getIntendedRecoveryDate() {
        return intendedRecoveryDate;
    }

    public void setIntendedRecoveryDate(String intendedRecoveryDate) {
        this.intendedRecoveryDate = intendedRecoveryDate;
    }

    public int getIntendedSoakTimeInYears() {
        return intendedSoakTimeInYears;
    }

    public void setIntendedSoakTimeInYears(int intendedSoakTime) {
        this.intendedSoakTimeInYears = intendedSoakTime;
    }

    public String getContinentOcean() {
        return continentOcean;
    }

    public void setContinentOcean(String continentOcean) {
        this.continentOcean = continentOcean;
    }

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

    public float getDecimalLatitude() {
        return decimalLatitude;
    }

    public void setDecimalLatitude(float latitude) {
        this.decimalLatitude = latitude;
    }

    public float getDecimalLongitude() {
        return decimalLongitude;
    }

    public void setDecimalLongitude(float longitude) {
        this.decimalLongitude = longitude;
    }

    public int getDepthInMeters() {
        return depthInMeters;
    }

    public void setDepthInMeters(int depth) {
        this.depthInMeters = depth;
    }

    public Integer getDepthOfBottomMeters() {
        return depthOfBottomMeters;
    }

    public void setDepthOfBottomMeters(Integer depthOfBottom) {
        this.depthOfBottomMeters = depthOfBottom;
    }

    public Integer getErrorRadius() {
        return errorRadius;
    }

    public void setErrorRadius(Integer errorRadius) {
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

    @Column(columnDefinition = "text")
    public String getSiteDetails() {
        return siteDetails;
    }

    public void setSiteDetails(String siteDetails) {
        this.siteDetails = siteDetails;
    }

    public String getReplicateLabel() {
        return replicateLabel;
    }

    public void setReplicateLabel(String replicateLabel) {
        this.replicateLabel = replicateLabel;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "expeditionId")
    @JsonBackReference
    public ArmsExpedition getArmsExpedition() {
        return armsExpedition;
    }

    public void setArmsExpedition(ArmsExpedition armsExpedition) {
        this.armsExpedition = armsExpedition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Deployment)) return false;

        Deployment that = (Deployment) o;

        if (this.getId() != 0 && that.getId() != 0)
            return this.getId() == that.getId();

        if (getNumLayers() != that.getNumLayers()) return false;
        if (getIntendedSoakTimeInYears() != that.getIntendedSoakTimeInYears()) return false;
        if (Float.compare(that.getDecimalLatitude(), getDecimalLatitude()) != 0) return false;
        if (Float.compare(that.getDecimalLongitude(), getDecimalLongitude()) != 0) return false;
        if (getDepthInMeters() != that.getDepthInMeters()) return false;
        if (getArmsModel() != null ? !getArmsModel().equals(that.getArmsModel()) : that.getArmsModel() != null)
            return false;
        if (getAttachmentMethod() != null ? !getAttachmentMethod().equals(that.getAttachmentMethod()) : that.getAttachmentMethod() != null)
            return false;
        if (getDeploymentId() != null ? !getDeploymentId().equals(that.getDeploymentId()) : that.getDeploymentId() != null)
            return false;
        if (hasScrubbieLayer != null ? !hasScrubbieLayer.equals(that.hasScrubbieLayer) : that.hasScrubbieLayer != null)
            return false;
        if (laminates != null ? !laminates.equals(that.laminates) : that.laminates != null) return false;
        if (getNewOrReused() != null ? !getNewOrReused().equals(that.getNewOrReused()) : that.getNewOrReused() != null)
            return false;
        if (weightsAttached != null ? !weightsAttached.equals(that.weightsAttached) : that.weightsAttached != null)
            return false;
        if (getDataEntryPersonInCharge() != null ? !getDataEntryPersonInCharge().equals(that.getDataEntryPersonInCharge()) : that.getDataEntryPersonInCharge() != null)
            return false;
        if (getDeploymentPersonInCharge() != null ? !getDeploymentPersonInCharge().equals(that.getDeploymentPersonInCharge()) : that.getDeploymentPersonInCharge() != null)
            return false;
        if (getDurationToProcessing() != null ? !getDurationToProcessing().equals(that.getDurationToProcessing()) : that.getDurationToProcessing() != null)
            return false;
        if (getNotes() != null ? !getNotes().equals(that.getNotes()) : that.getNotes() != null) return false;
        if (getProcessingPersonInCharge() != null ? !getProcessingPersonInCharge().equals(that.getProcessingPersonInCharge()) : that.getProcessingPersonInCharge() != null)
            return false;
        if (getRecoveryPersonInCharge() != null ? !getRecoveryPersonInCharge().equals(that.getRecoveryPersonInCharge()) : that.getRecoveryPersonInCharge() != null)
            return false;
        if (getRemovalProtocol() != null ? !getRemovalProtocol().equals(that.getRemovalProtocol()) : that.getRemovalProtocol() != null)
            return false;
        if (intentToBarcode != null ? !intentToBarcode.equals(that.intentToBarcode) : that.intentToBarcode != null)
            return false;
        if (intentToCollectOtherDatatypes != null ? !intentToCollectOtherDatatypes.equals(that.intentToCollectOtherDatatypes) : that.intentToCollectOtherDatatypes != null)
            return false;
        if (intentToMetabarcode != null ? !intentToMetabarcode.equals(that.intentToMetabarcode) : that.intentToMetabarcode != null)
            return false;
        if (intentToPhotographPlates != null ? !intentToPhotographPlates.equals(that.intentToPhotographPlates) : that.intentToPhotographPlates != null)
            return false;
        if (intentToPhotographSpecimens != null ? !intentToPhotographSpecimens.equals(that.intentToPhotographSpecimens) : that.intentToPhotographSpecimens != null)
            return false;
        if (getNumReplicatesInSet() != null ? !getNumReplicatesInSet().equals(that.getNumReplicatesInSet()) : that.getNumReplicatesInSet() != null)
            return false;
        if (getPhotoUrl() != null ? !getPhotoUrl().equals(that.getPhotoUrl()) : that.getPhotoUrl() != null)
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
        if (getDepthOfBottomMeters() != null ? !getDepthOfBottomMeters().equals(that.getDepthOfBottomMeters()) : that.getDepthOfBottomMeters() != null)
            return false;
        if (getErrorRadius() != null ? !getErrorRadius().equals(that.getErrorRadius()) : that.getErrorRadius() != null)
            return false;
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
        if (getStationId() != null ? !getStationId().equals(that.getStationId()) : that.getStationId() != null)
            return false;
        if (getSubstrateType() != null ? !getSubstrateType().equals(that.getSubstrateType()) : that.getSubstrateType() != null)
            return false;
        return getArmsExpedition() != null ? getArmsExpedition().equals(that.getArmsExpedition()) : that.getArmsExpedition() == null;

    }

    @Override
    public int hashCode() {
        int result = getArmsModel() != null ? getArmsModel().hashCode() : 0;
        result = 31 * result + (getAttachmentMethod() != null ? getAttachmentMethod().hashCode() : 0);
        result = 31 * result + (getDeploymentId() != null ? getDeploymentId().hashCode() : 0);
        result = 31 * result + (hasScrubbieLayer != null ? hasScrubbieLayer.hashCode() : 0);
        result = 31 * result + (laminates != null ? laminates.hashCode() : 0);
        result = 31 * result + (getNewOrReused() != null ? getNewOrReused().hashCode() : 0);
        result = 31 * result + getNumLayers();
        result = 31 * result + (weightsAttached != null ? weightsAttached.hashCode() : 0);
        result = 31 * result + (getDataEntryPersonInCharge() != null ? getDataEntryPersonInCharge().hashCode() : 0);
        result = 31 * result + (getDeploymentPersonInCharge() != null ? getDeploymentPersonInCharge().hashCode() : 0);
        result = 31 * result + (getDurationToProcessing() != null ? getDurationToProcessing().hashCode() : 0);
        result = 31 * result + (getNotes() != null ? getNotes().hashCode() : 0);
        result = 31 * result + (getProcessingPersonInCharge() != null ? getProcessingPersonInCharge().hashCode() : 0);
        result = 31 * result + (getRecoveryPersonInCharge() != null ? getRecoveryPersonInCharge().hashCode() : 0);
        result = 31 * result + (getRemovalProtocol() != null ? getRemovalProtocol().hashCode() : 0);
        result = 31 * result + (intentToBarcode != null ? intentToBarcode.hashCode() : 0);
        result = 31 * result + (intentToCollectOtherDatatypes != null ? intentToCollectOtherDatatypes.hashCode() : 0);
        result = 31 * result + (intentToMetabarcode != null ? intentToMetabarcode.hashCode() : 0);
        result = 31 * result + (intentToPhotographPlates != null ? intentToPhotographPlates.hashCode() : 0);
        result = 31 * result + (intentToPhotographSpecimens != null ? intentToPhotographSpecimens.hashCode() : 0);
        result = 31 * result + (getNumReplicatesInSet() != null ? getNumReplicatesInSet().hashCode() : 0);
        result = 31 * result + (getPhotoUrl() != null ? getPhotoUrl().hashCode() : 0);
        result = 31 * result + (getActualDeploymentDate() != null ? getActualDeploymentDate().hashCode() : 0);
        result = 31 * result + (getActualDeploymentTimeOfDay() != null ? getActualDeploymentTimeOfDay().hashCode() : 0);
        result = 31 * result + (getActualRecoveryDate() != null ? getActualRecoveryDate().hashCode() : 0);
        result = 31 * result + (getActualRecoveryTimeOfDay() != null ? getActualRecoveryTimeOfDay().hashCode() : 0);
        result = 31 * result + (getIntendedDeploymentDate() != null ? getIntendedDeploymentDate().hashCode() : 0);
        result = 31 * result + (getIntendedRecoveryDate() != null ? getIntendedRecoveryDate().hashCode() : 0);
        result = 31 * result + getIntendedSoakTimeInYears();
        result = 31 * result + (getContinentOcean() != null ? getContinentOcean().hashCode() : 0);
        result = 31 * result + (getCountry() != null ? getCountry().hashCode() : 0);
        result = 31 * result + (getCounty() != null ? getCounty().hashCode() : 0);
        result = 31 * result + (getDecimalLatitude() != +0.0f ? Float.floatToIntBits(getDecimalLatitude()) : 0);
        result = 31 * result + (getDecimalLongitude() != +0.0f ? Float.floatToIntBits(getDecimalLongitude()) : 0);
        result = 31 * result + getDepthInMeters();
        result = 31 * result + (getDepthOfBottomMeters() != null ? getDepthOfBottomMeters().hashCode() : 0);
        result = 31 * result + (getErrorRadius() != null ? getErrorRadius().hashCode() : 0);
        result = 31 * result + (getHabitat() != null ? getHabitat().hashCode() : 0);
        result = 31 * result + (getHorizontalDatum() != null ? getHorizontalDatum().hashCode() : 0);
        result = 31 * result + (getIsland() != null ? getIsland().hashCode() : 0);
        result = 31 * result + (getIslandGroup() != null ? getIslandGroup().hashCode() : 0);
        result = 31 * result + (getLocality() != null ? getLocality().hashCode() : 0);
        result = 31 * result + (getRegion() != null ? getRegion().hashCode() : 0);
        result = 31 * result + (getStateProvince() != null ? getStateProvince().hashCode() : 0);
        result = 31 * result + (getStationId() != null ? getStationId().hashCode() : 0);
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
