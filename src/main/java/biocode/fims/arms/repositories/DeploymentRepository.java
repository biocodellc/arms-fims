package biocode.fims.arms.repositories;

import biocode.fims.arms.entities.Deployment;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * This repositories provides CRUD operations for {@link Deployment} objects
 */
@Transactional("armsTransactionManager")
@org.springframework.stereotype.Repository
public interface DeploymentRepository extends Repository<Deployment, Integer> {

    @Modifying
    void deleteByArmsExpeditionExpeditionId(int expeditionId);

    Deployment findOneByArmsExpeditionExpeditionIdAndDeploymentId(int expeditionId, String deploymentId);
}