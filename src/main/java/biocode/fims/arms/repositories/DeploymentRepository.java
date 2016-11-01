package biocode.fims.arms.repositories;

import biocode.fims.arms.entities.Deployment;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * This repositories provides CRUD operations for {@link Deployment} objects
 */
@Transactional("armsTransactionManager")
@org.springframework.stereotype.Repository
public interface DeploymentRepository extends Repository<Deployment, Integer>, QueryDslPredicateExecutor<Deployment>, DeploymentBulkUploadRepository {

    @Modifying
    void deleteByArmsExpeditionExpeditionId(int expeditionId);

    Deployment findOneByArmsExpeditionExpeditionIdAndDeploymentId(int expeditionId, String deploymentId);

    @Query(value = "SELECT column_name FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = \"deployments\"",
            nativeQuery = true)
    List<String> getTableColumns();

    List<Deployment> findAllByArmsExpeditionExpeditionId(int expeditionId);
}