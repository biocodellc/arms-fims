package biocode.fims.arms.repositories;

import biocode.fims.arms.entities.ArmsExpedition;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * This repositories provides CRUD operations for {@link ArmsExpedition} objects
 */
@Transactional("armsTransactionManager")
public interface ArmsExpeditionRepository extends Repository<ArmsExpedition, Integer> {

    @Modifying
    void deleteByExpeditionId(int armsExpedition);

    void save(ArmsExpedition armsExpedition);

    @EntityGraph(value = "withDeployments", type = EntityGraph.EntityGraphType.FETCH)
    ArmsExpedition findByExpeditionId(int armsExpeditionId);

    Set<ArmsExpedition> findAll();

    List<ArmsExpedition> findByExpeditionIdIn(List<Integer> expeditionIds);
}