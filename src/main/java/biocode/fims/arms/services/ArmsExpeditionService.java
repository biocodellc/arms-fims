package biocode.fims.arms.services;

import biocode.fims.arms.entities.ArmsExpedition;
import biocode.fims.arms.repositories.ArmsExpeditionRepository;
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
@Transactional
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
    public void create(ArmsExpedition armsExpedition) {
        expeditionService.create(armsExpedition.getExpedition(), null);

        // now that the Expedition's id has been set
        armsExpedition.setExpeditionId(armsExpedition.getExpedition().getExpeditionId());
        armsExpedition.getExpedition().setExpeditionTitle("ARMS Project " + armsExpedition.getExpeditionId());

        armsExpeditionRepository.save(armsExpedition);
    }


    public void update(ArmsExpedition armsExpedition) {
        // TODO do I need to manually update the expedition? Or is it cascaded?
        armsExpeditionRepository.save(armsExpedition);
    }

    public ArmsExpedition getArmsExpedition(int armsExpeditionId) {
        Expedition expedition = expeditionService.getExpedition(armsExpeditionId);

        ArmsExpedition armsExpedition = armsExpeditionRepository.findByArmsExpeditionId(armsExpeditionId);
        armsExpedition.setExpedition(expedition);

        return armsExpedition;
    }

    public Set<ArmsExpedition> findAll() {
        Set<ArmsExpedition> armsExpeditions = armsExpeditionRepository.findAll();

        for (ArmsExpedition armsExpedition: armsExpeditions) {
            armsExpedition.setExpedition(expeditionService.getExpedition((armsExpedition.getExpeditionId())));
        }

        return armsExpeditions;
    }
}
