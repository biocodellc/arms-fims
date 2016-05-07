package biocode.fims.arms.entities;

import biocode.fims.entities.Expedition;

import javax.persistence.*;
import java.net.URI;
import java.util.Set;

/**
 * ArmsProject domain object. An ArmsProject has a one-to-one unidirectional relationship to
 * a biocode-fims {@link Expedition}.
 */
@Entity
@Table(name = "armsProject")
public class ArmsProject {

    private int id;
    private String principalInvestigator;
    private String contactName;
    private String contactEmail;
    private String fundingSource;
    private int envisionedDuration;
    private String geographicScope;
    private String goals;
    private String leadOrganization;
    private URI identifier;

    private Expedition expedition;
    private Set<Deployment> deployments;

    class ArmsProjectBuilder {
        private String principalInvestigator;
        private URI identifier;
        private Expedition expedition;

        private String contactName;
        private String contactEmail;
        private String fundingSource;
        private int envisionedDuration;
        private String geographicScope;
        private String goals;
        private String leadOrganization;

        public ArmsProjectBuilder(String principalInvestigator, URI identifier, Expedition expedition) {
            this.principalInvestigator = principalInvestigator;
            this.identifier = identifier;
            this.expedition = expedition;
        }

        public ArmsProjectBuilder contactName(String contactName) {
            this.contactName = contactName;
            return this;
        }

        public ArmsProjectBuilder contactEmail(String contactEmail) {
            this.contactEmail = contactEmail;
            return this;
        }

        public ArmsProjectBuilder fundingSource(String fundingSource) {
            this.fundingSource = fundingSource;
            return this;
        }

        public ArmsProjectBuilder envisionedDuration(int envisionedDuration) {
            this.envisionedDuration = envisionedDuration;
            return this;
        }

        public ArmsProjectBuilder geographicScope(String geographicScope) {
            this.geographicScope = geographicScope;
            return this;
        }

        public ArmsProjectBuilder goals(String goals) {
            this.goals = goals;
            return this;
        }

        public ArmsProjectBuilder leadOrganization(String leadOrganization) {
            this.leadOrganization = leadOrganization;
            return this;
        }

        public ArmsProject build() {
            return new ArmsProject(this);
        }
    }

    // needed for hibernate
    private ArmsProject() {}

    public ArmsProject(ArmsProjectBuilder builder) {
        this.principalInvestigator = builder.principalInvestigator;
        this.identifier = builder.identifier;
        this.expedition = builder.expedition;
        this.contactName = builder.contactName;
        this.contactEmail = builder.contactEmail;
        this.fundingSource = builder.fundingSource;
        this.envisionedDuration = builder.envisionedDuration;
        this.geographicScope = builder.geographicScope;
        this.goals = builder.goals;
        this.leadOrganization = builder.leadOrganization;
    }

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
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

    public URI getIdentifier() {
        return identifier;
    }

    private void setIdentifier(URI identifier) {
        // todo should this just be set when the expedition is passed in?
        this.identifier = identifier;
    }

    public Expedition getExpedition() {
        return expedition;
    }

    private void setExpedition(Expedition expedition) {
        this.expedition = expedition;
    }

    @OneToMany(targetEntity = Deployment.class,
            mappedBy = "armsProject",
            fetch = FetchType.LAZY
    )
    public Set<Deployment> getDeployments() {
        return deployments;
    }

    private void setDeployments(Set<Deployment> deployments) {
        this.deployments = deployments;
    }
}
