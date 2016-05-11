package biocode.fims.arms.repositories;

import biocode.fims.arms.entities.ArmsExpedition;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * This repositories provides CRUD operations for {@link ArmsExpedition} objects
 */
@Transactional("armsTransactionManager")
public interface ArmsExpeditionRepository extends Repository<ArmsExpedition, Integer> {

    @Modifying
    void delete(ArmsExpedition armsExpedition);

    void save(ArmsExpedition armsExpedition);

    ArmsExpedition findByExpeditionId(int armsExpeditionId);

    Set<ArmsExpedition> findAll();
}