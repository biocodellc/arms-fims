package biocode.fims.arms.services;

import biocode.fims.arms.entities.Deployment;
import biocode.fims.arms.repositories.DeploymentRepository;
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

}
