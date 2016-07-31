package biocode.fims.arms.services;

import biocode.fims.arms.entities.ArmsExpedition;
import biocode.fims.arms.entities.Deployment;
import biocode.fims.arms.query.DeploymentPredicatesBuilder;
import biocode.fims.arms.repositories.DeploymentRepository;
import biocode.fims.entities.Bcid;
import biocode.fims.mysql.query.Query;
import biocode.fims.service.BcidService;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for handling {@link Deployment} persistence
 */
@Service
@Transactional("armsTransactionManager")
public class DeploymentService {
    private final DeploymentRepository deploymentRepository;
    private final BcidService bcidService;

    @Autowired
    public DeploymentService(DeploymentRepository deploymentRepository, BcidService bcidService) {
        this.deploymentRepository = deploymentRepository;
        this.bcidService = bcidService;
    }

    /**
     * Delete all {@link Deployment}'s for a given expeditionId
     * @param expeditionId
     */
    public void deleteAll(int expeditionId) {
        deploymentRepository.deleteByArmsExpeditionExpeditionId(expeditionId);
    }

    public Deployment getDeployment(int expeditionId, String deploymentId) {
        return deploymentRepository.findOneByArmsExpeditionExpeditionIdAndDeploymentId(expeditionId, deploymentId);
    }

    public Deployment getDeployment(String rootIdentifier, String deploymentId) {
        Deployment deployment = null;
        Bcid rootEntity = bcidService.getBcid(rootIdentifier);

        if (rootEntity.getExpedition() != null) {
            deployment = deploymentRepository.findOneByArmsExpeditionExpeditionIdAndDeploymentId(
                    rootEntity.getExpedition().getExpeditionId(), deploymentId);
        }

        return deployment;

    }

    public Iterable<Deployment> query(Query query) {
        DeploymentPredicatesBuilder builder = new DeploymentPredicatesBuilder(query.getCriterion());
        Predicate predicate = builder.build();

        return deploymentRepository.findAll(predicate);

    }
}
