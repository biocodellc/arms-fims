package biocode.fims.arms.services;

import biocode.fims.arms.entities.Deployment;
import biocode.fims.arms.entities.QDeployment;
import biocode.fims.arms.query.DeploymentPredicatesBuilder;
import biocode.fims.arms.repositories.DeploymentRepository;
import biocode.fims.mysql.query.Query;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import org.glassfish.hk2.api.IterableProvider;
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

    @Autowired
    public DeploymentService(DeploymentRepository deploymentRepository) {
        this.deploymentRepository = deploymentRepository;
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

    public Iterable<Deployment> query(Query query) {
        DeploymentPredicatesBuilder builder = new DeploymentPredicatesBuilder(query.getCriterion());
        Predicate predicate = builder.build();

        return deploymentRepository.findAll(predicate);

    }
}
