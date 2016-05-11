package biocode.fims.arms.entities;

import biocode.fims.entities.Expedition;
import biocode.fims.fimsExceptions.FimsRuntimeException;

import javax.persistence.*;
import java.net.URI;
import java.util.Set;

/**
 * ArmsExpedition domain object. An ArmsExpedition has a one-to-one unidirectional relationship to
 * a biocode-fims {@link Expedition}.
 */
@Entity
@Table(name = "armsExpedition")
public class ArmsExpedition {

    private int expeditionId;
    private String principalInvestigator;
    private String contactName;
    private String contactEmail;
    private String fundingSource;
    private int envisionedDuration;
    private String geographicScope;
    private String goals;
    private String leadOrganization;
//    private URI identifier;

    private Expedition expedition;
    private Set<Deployment> deployments;

    public static class ArmsExpeditionBuilder {
        private String principalInvestigator;
//        private URI identifier;
        private Expedition expedition;

        private String contactName;
        private String contactEmail;
        private String fundingSource;
        private int envisionedDuration;
        private String geographicScope;
        private String goals;
        private String leadOrganization;

        public ArmsExpeditionBuilder(String principalInvestigator, Expedition expedition) {
            this.principalInvestigator = principalInvestigator;
//            this.identifier = identifier;
            this.expedition = expedition;
        }

        public ArmsExpeditionBuilder contactName(String contactName) {
            this.contactName = contactName;
            return this;
        }

        public ArmsExpeditionBuilder contactEmail(String contactEmail) {
            this.contactEmail = contactEmail;
            return this;
        }

        public ArmsExpeditionBuilder fundingSource(String fundingSource) {
            this.fundingSource = fundingSource;
            return this;
        }

        public ArmsExpeditionBuilder envisionedDuration(int envisionedDuration) {
            this.envisionedDuration = envisionedDuration;
            return this;
        }

        public ArmsExpeditionBuilder geographicScope(String geographicScope) {
            this.geographicScope = geographicScope;
            return this;
        }

        public ArmsExpeditionBuilder goals(String goals) {
            this.goals = goals;
            return this;
        }

        public ArmsExpeditionBuilder leadOrganization(String leadOrganization) {
            this.leadOrganization = leadOrganization;
            return this;
        }

        private boolean validArmsExpedition() {
            if (principalInvestigator == null || expedition == null || contactName == null || contactEmail == null ||
                    fundingSource == null || envisionedDuration == 0 || geographicScope == null ||
                    goals == null || leadOrganization == null) {
                return false;
            }
            return true;
        }

        public ArmsExpedition build() {
            if (!validArmsExpedition())
                throw new FimsRuntimeException("", "Trying to create an invalid expedition. " +
                        "principalInvestigator, expedition, contactName, contactEmail, fundingSource, envisionedDuration," +
                        "geographicScope, goals, and leadOrganization must not be null", 500);

            return new ArmsExpedition(this);
        }
    }

    // needed for hibernate
    private ArmsExpedition() {}

    public ArmsExpedition(ArmsExpeditionBuilder builder) {
//        this.expeditionId = builder.expedition.getExpeditionId();
        this.principalInvestigator = builder.principalInvestigator;
//        this.identifier = builder.identifier;
        this.expedition = builder.expedition;
        this.contactName = builder.contactName;
        this.contactEmail = builder.contactEmail;
        this.fundingSource = builder.fundingSource;
        this.envisionedDuration = builder.envisionedDuration;
        this.geographicScope = builder.geographicScope;
        this.goals = builder.goals;
        this.leadOrganization = builder.leadOrganization;
    }

    @Id
    public int getExpeditionId() {
        return expeditionId;
    }

    public void setExpeditionId(int id) {
        if (expeditionId == 0)
            this.expeditionId = id;
    }

    public String getPrincipalInvestigator() {
        return principalInvestigator;
    }

    public void setPrincipalInvestigator(String principalInvestigator) {
        this.principalInvestigator = principalInvestigator;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getFundingSource() {
        return fundingSource;
    }

    public void setFundingSource(String fundingSource) {
        this.fundingSource = fundingSource;
    }

    public int getEnvisionedDuration() {
        return envisionedDuration;
    }

    public void setEnvisionedDuration(int envisionedDuration) {
        this.envisionedDuration = envisionedDuration;
    }

    public String getGeographicScope() {
        return geographicScope;
    }

    public void setGeographicScope(String geographicScope) {
        this.geographicScope = geographicScope;
    }

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public String getLeadOrganization() {
        return leadOrganization;
    }

    public void setLeadOrganization(String leadOrganization) {
        this.leadOrganization = leadOrganization;
    }

//    public URI getIdentifier() {
//        return identifier;
//    }
//
//    private void setIdentifier(URI identifier) {
//        // todo should this just be set when the expedition is passed in?
//        this.identifier = identifier;
//    }

    @Transient
    public Expedition getExpedition() {
        return expedition;
    }

    public void setExpedition(Expedition expedition) {
        if (expedition == null)
            this.expedition = expedition;
    }

    @OneToMany(targetEntity = Deployment.class,
            mappedBy = "armsExpedition",
            fetch = FetchType.LAZY
    )
    public Set<Deployment> getDeployments() {
        return deployments;
    }

    private void setDeployments(Set<Deployment> deployments) {
        this.deployments = deployments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArmsExpedition that = (ArmsExpedition) o;

        if (getExpeditionId() != that.getExpeditionId()) return false;
        if (getEnvisionedDuration() != that.getEnvisionedDuration()) return false;
        if (!getPrincipalInvestigator().equals(that.getPrincipalInvestigator())) return false;
        if (!getContactName().equals(that.getContactName())) return false;
        if (!getContactEmail().equals(that.getContactEmail())) return false;
        if (!getFundingSource().equals(that.getFundingSource())) return false;
        if (!getGeographicScope().equals(that.getGeographicScope())) return false;
        if (!getGoals().equals(that.getGoals())) return false;
        return getLeadOrganization().equals(that.getLeadOrganization());

    }

    @Override
    public int hashCode() {
        int result = getExpeditionId();
        result = 31 * result + getPrincipalInvestigator().hashCode();
        result = 31 * result + getContactName().hashCode();
        result = 31 * result + getContactEmail().hashCode();
        result = 31 * result + getFundingSource().hashCode();
        result = 31 * result + getEnvisionedDuration();
        result = 31 * result + getGeographicScope().hashCode();
        result = 31 * result + getGoals().hashCode();
        result = 31 * result + getLeadOrganization().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ArmsExpedition{" +
                "leadOrganization='" + leadOrganization + '\'' +
                ", principalInvestigator='" + principalInvestigator + '\'' +
                ", contactName='" + contactName + '\'' +
                ", contactEmail='" + contactEmail + '\'' +
                ", fundingSource='" + fundingSource + '\'' +
                ", envisionedDuration=" + envisionedDuration +
                ", geographicScope='" + geographicScope + '\'' +
                ", goals='" + goals + '\'' +
                ", expedition=" + expedition +
                '}';
    }
}
