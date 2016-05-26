package biocode.fims.arms.services;

import biocode.fims.arms.entities.ArmsExpedition;
import biocode.fims.arms.repositories.ArmsExpeditionRepository;
import biocode.fims.digester.Mapping;
import biocode.fims.entities.Expedition;
import biocode.fims.service.ExpeditionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Service class for handling {@link biocode.fims.arms.entities.ArmsExpedition} persistence
 */
@Service
@Transactional("armsTransactionManager")
public class ArmsExpeditionService {
    private final ExpeditionService expeditionService;
    private final ArmsExpeditionRepository armsExpeditionRepository;

    @Autowired
    public ArmsExpeditionService(ArmsExpeditionRepository armsExpeditionRepository, ExpeditionService expeditionService) {
        this.armsExpeditionRepository = armsExpeditionRepository;
        this.expeditionService = expeditionService;
    }

    /**
     * Create an ARMS expedition. This involves first creating a Biocode {@link Expedition}, then setting
     * the ArmsExpedition.expeditionId field. Then persisting the {@link ArmsExpedition}
     * @param armsExpedition
     */
    public void create(ArmsExpedition armsExpedition, int userId, int projectId, Mapping mapping) {
        expeditionService.create(armsExpedition.getExpedition(), userId, projectId, null, mapping);

        try {
            // now that the Expedition's id has been set
            armsExpedition.setExpeditionId(armsExpedition.getExpedition().getExpeditionId());
            armsExpedition.getExpedition().setExpeditionTitle(
                    "(" + armsExpedition.getExpedition().getExpeditionCode() + ") ARMS ProjectId: " + armsExpedition.getExpeditionId());
            // need to manually call update as expedition and armsExpedition are in different entityManagers/databases
            expeditionService.update(armsExpedition.getExpedition());

            armsExpeditionRepository.save(armsExpedition);
        } catch (Exception e) {
            // on any exception, delete the expedition
            expeditionService.delete(armsExpedition.getExpeditionId());
        }
    }


    public void update(ArmsExpedition armsExpedition) {
        armsExpeditionRepository.save(armsExpedition);
        expeditionService.update(armsExpedition.getExpedition());
    }

    public ArmsExpedition getArmsExpedition(int armsExpeditionId) {
        Expedition expedition = expeditionService.getExpedition(armsExpeditionId);

        ArmsExpedition armsExpedition = armsExpeditionRepository.findByExpeditionId(armsExpeditionId);
        armsExpedition.setExpedition(expedition);

        return armsExpedition;
    }

    public Set<ArmsExpedition> findAll() {
        Set<ArmsExpedition> armsExpeditions = armsExpeditionRepository.findAll();

        for (ArmsExpedition armsExpedition: armsExpeditions) {
            armsExpedition.setExpedition(expeditionService.getExpedition(armsExpedition.getExpeditionId()));
        }

        return armsExpeditions;
    }
}
