package fr.syncrase.ecosyst.feature.add_plante.repository;

import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import fr.syncrase.ecosyst.repository.CronquistRankRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CronquistWriter {

    private final Logger log = LoggerFactory.getLogger(CronquistWriter.class);

    private CronquistRankRepository cronquistRankRepository;

    @Autowired
    public void setCronquistRankRepository(CronquistRankRepository cronquistRankRepository) {
        this.cronquistRankRepository = cronquistRankRepository;
    }

    public CronquistClassificationBranch saveClassification(CronquistClassificationBranch newClassification) {
        log.info(String.format("Save the classification %s", newClassification));
        if (newClassification.getClassificationSet() == null || newClassification.getClassificationSet().size() == 0) {
            log.error("No classification to save");
            return null;
        }

        // TODO Check children and parent
        cronquistRankRepository.saveAll(newClassification);
        return newClassification;
    }
}
