package biocode.fims.arms.services;

import biocode.fims.arms.entities.ArmsExpedition;
import biocode.fims.arms.repositories.ArmsExpeditionRepository;
import biocode.fims.digester.Mapping;
import biocode.fims.entities.Expedition;
import biocode.fims.service.ExpeditionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

    public void delete(String expeditionCode, int projectId) {
        Expedition expedition = expeditionService.getExpedition(expeditionCode, projectId);

        if (expedition != null) {
            armsExpeditionRepository.deleteByExpeditionId(expedition.getExpeditionId());
            expeditionService.delete(expedition.getExpeditionId());
        }
    }

    public ArmsExpedition getArmsExpedition(int armsExpeditionId) {
        Expedition expedition = expeditionService.getExpedition(armsExpeditionId);

        ArmsExpedition armsExpedition = armsExpeditionRepository.findByExpeditionId(armsExpeditionId);
        armsExpedition.setExpedition(expedition);

        return armsExpedition;
    }

    public ArmsExpedition getArmsExpeditionByIdentifier(String identifier) {
        Expedition expedition = expeditionService.getExpedition(identifier);

        ArmsExpedition armsExpedition = armsExpeditionRepository.findByExpeditionId(expedition.getExpeditionId());
        armsExpedition.setExpedition(expedition);

        return armsExpedition;
    }

    public List<ArmsExpedition> getPublicExpeditions(int projectId) {
        List<Expedition> publicExpeditions = expeditionService.getPublicExpeditions(projectId);

        return getArmsExpeditions(publicExpeditions);
    }

    private List<ArmsExpedition> getArmsExpeditions(List<Expedition> expeditions) {
        List<Integer> expeditionIds = new ArrayList<>();

        for (Expedition expedition: expeditions) {
            expeditionIds.add(expedition.getExpeditionId());
        }

        List<ArmsExpedition> armsExpeditions = armsExpeditionRepository.findByExpeditionIdIn(expeditionIds);

        for (ArmsExpedition armsExpedition: armsExpeditions) {
            for (Expedition expedition: expeditions) {
                if (armsExpedition.getExpeditionId() == expedition.getExpeditionId()) {
                    armsExpedition.setExpedition(expedition);
                }
            }
        }

        return armsExpeditions;
    }

    public List<ArmsExpedition> getArmsExpeditions(int projectId, int userId, boolean includePrivate) {
        List<Expedition> expeditions = expeditionService.getExpeditionsForUser(projectId, userId, includePrivate);

        return getArmsExpeditions(expeditions);
    }
}
