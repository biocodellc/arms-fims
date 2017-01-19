package biocode.fims.arms.entities;

import biocode.fims.entities.Expedition;
import biocode.fims.fimsExceptions.FimsRuntimeException;
import biocode.fims.serializers.Views;
import biocode.fims.serializers.Views.Detailed;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.persistence.*;
import java.util.Set;

/**
 * ArmsExpedition domain object. An ArmsExpedition has a one-to-one unidirectional relationship to
 * a biocode-fims {@link Expedition}.
 */
@Entity
@Table(name = "armsExpeditions")
@NamedEntityGraph(name = "withDeployments",
        attributeNodes = @NamedAttributeNode("deployments"))
public class ArmsExpedition {

    public static class withDeploymentsView extends Detailed {}

    private int expeditionId;
    private String principalInvestigator;
    private String contactName;
    private String contactEmail;
    private String fundingSource;
    private int envisionedDuration;
    private String geographicScope;
    private String goals;
    private String leadOrganization;

    private Expedition expedition;
    private Set<Deployment> deployments;

    public static class ArmsExpeditionBuilder {
        private String principalInvestigator;
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
    ArmsExpedition() {}

    public ArmsExpedition(ArmsExpeditionBuilder builder) {
        this.principalInvestigator = builder.principalInvestigator;
        this.expedition = builder.expedition;
        this.contactName = builder.contactName;
        this.contactEmail = builder.contactEmail;
        this.fundingSource = builder.fundingSource;
        this.envisionedDuration = builder.envisionedDuration;
        this.geographicScope = builder.geographicScope;
        this.goals = builder.goals;
        this.leadOrganization = builder.leadOrganization;
    }

    @JsonView(Views.Summary.class)
    @Id
    @Column(nullable = false)
    public int getExpeditionId() {
        return expeditionId;
    }

    public void setExpeditionId(int id) {
        if (expeditionId == 0)
            this.expeditionId = id;
    }

    @JsonView(Views.Detailed.class)
    @Column(nullable = false)
    public String getPrincipalInvestigator() {
        return principalInvestigator;
    }

    public void setPrincipalInvestigator(String principalInvestigator) {
        this.principalInvestigator = principalInvestigator;
    }

    @JsonView(Views.Detailed.class)
    @Column(nullable = false)
    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    @JsonView(Views.Detailed.class)
    @Column(nullable = false)
    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    @JsonView(Views.Detailed.class)
    @Column(nullable = false)
    public String getFundingSource() {
        return fundingSource;
    }

    public void setFundingSource(String fundingSource) {
        this.fundingSource = fundingSource;
    }

    @JsonView(Views.Detailed.class)
    @Column(nullable = false)
    public int getEnvisionedDuration() {
        return envisionedDuration;
    }

    public void setEnvisionedDuration(int envisionedDuration) {
        this.envisionedDuration = envisionedDuration;
    }

    @JsonView(Views.Detailed.class)
    @Column(nullable = false)
    public String getGeographicScope() {
        return geographicScope;
    }

    public void setGeographicScope(String geographicScope) {
        this.geographicScope = geographicScope;
    }

    @JsonView(Views.Detailed.class)
    @Column(columnDefinition = "text not null")
    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    @JsonView(Views.Detailed.class)
    @Column(nullable = false)
    public String getLeadOrganization() {
        return leadOrganization;
    }

    public void setLeadOrganization(String leadOrganization) {
        this.leadOrganization = leadOrganization;
    }

    @JsonView(Views.Summary.class)
    @Transient
    public Expedition getExpedition() {
        return expedition;
    }

    public void setExpedition(Expedition expedition) {
        if (this.expedition == null)
            this.expedition = expedition;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonView(withDeploymentsView.class)
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
        if (!(o instanceof ArmsExpedition)) return false;

        ArmsExpedition that = (ArmsExpedition) o;

        return this.getExpedition().equals(that.getExpedition());
    }

    @Override
    public int hashCode() {
        return getExpedition().hashCode();
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
