package fr.syncrase.ecosyst.feature.add_plante.repository;

import fr.syncrase.ecosyst.domain.Classification;
import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.domain.NomVernaculaire;
import fr.syncrase.ecosyst.domain.Plante;
import fr.syncrase.ecosyst.service.ClassificationQueryService;
import fr.syncrase.ecosyst.service.CronquistRankQueryService;
import fr.syncrase.ecosyst.service.NomVernaculaireQueryService;
import fr.syncrase.ecosyst.service.PlanteQueryService;
import fr.syncrase.ecosyst.service.criteria.ClassificationCriteria;
import fr.syncrase.ecosyst.service.criteria.CronquistRankCriteria;
import fr.syncrase.ecosyst.service.criteria.NomVernaculaireCriteria;
import fr.syncrase.ecosyst.service.criteria.PlanteCriteria;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlanteReader {

    private CronquistRankQueryService cronquistRankQueryService;

    private NomVernaculaireQueryService nomVernaculaireQueryService;

    private PlanteQueryService planteQueryService;

    private ClassificationQueryService classificationQueryService;

    @Autowired
    public void setClassificationQueryService(ClassificationQueryService classificationQueryService) {
        this.classificationQueryService = classificationQueryService;
    }


    @Autowired
    public void setCronquistRankQueryService(CronquistRankQueryService cronquistRankQueryService) {
        this.cronquistRankQueryService = cronquistRankQueryService;
    }

    @Autowired
    public void setNomVernaculaireQueryService(NomVernaculaireQueryService nomVernaculaireQueryService) {
        this.nomVernaculaireQueryService = nomVernaculaireQueryService;
    }

    @Autowired
    public void setPlanteQueryService(PlanteQueryService planteQueryService) {
        this.planteQueryService = planteQueryService;
    }

    public List<Plante> findPlantes(@NotNull String name) {
        PlanteCriteria planteCriteria = new PlanteCriteria();

        StringFilter nomFilter = new StringFilter();
        nomFilter.setEquals(name.toLowerCase());

        addNomsVernaculairesIdsIfAnyExists(planteCriteria, nomFilter);
        addClassificationIdsIfAnyExists(planteCriteria, nomFilter);

        if (planteCriteria.getClassificationId() != null || planteCriteria.getNomsVernaculairesId() != null) {
            return planteQueryService.findByCriteria(planteCriteria);
        }
        return List.of();
    }

    private void addClassificationIdsIfAnyExists(PlanteCriteria planteCriteria, StringFilter nomFilter) {
        CronquistRankCriteria cronquistRankCriteria = new CronquistRankCriteria();
        cronquistRankCriteria.setNom(nomFilter);
        List<CronquistRank> cronquistRankByCriteria = cronquistRankQueryService.findByCriteria(cronquistRankCriteria);
        if (cronquistRankByCriteria.size() != 0) {
            ClassificationCriteria classificationCriteria = new ClassificationCriteria();
            LongFilter cronquistIdFilter = new LongFilter();
            cronquistIdFilter.setEquals(cronquistRankByCriteria.get(0).getId());
            classificationCriteria.setCronquistId(cronquistIdFilter);
            List<Classification> classificationByCriteria = classificationQueryService.findByCriteria(classificationCriteria);
            if (classificationByCriteria.size() != 0) {
                LongFilter rankIdFilter = new LongFilter();
                rankIdFilter.setEquals(classificationByCriteria.get(0).getId());
                planteCriteria.setClassificationId(rankIdFilter);
            }
        }
    }

    private void addNomsVernaculairesIdsIfAnyExists(PlanteCriteria planteCriteria, StringFilter nomFilter) {
        NomVernaculaireCriteria nomCriteria = new NomVernaculaireCriteria();
        nomCriteria.setNom(nomFilter);
        List<Long> nomVernaculaireIds = nomVernaculaireQueryService.findByCriteria(nomCriteria).stream().map(NomVernaculaire::getId).collect(Collectors.toList());
        if (nomVernaculaireIds.size() != 0) {
            LongFilter nomVernaculaireIdFilter = new LongFilter();
            nomVernaculaireIdFilter.setIn(nomVernaculaireIds);
            planteCriteria.setNomsVernaculairesId(nomVernaculaireIdFilter);
        }
    }
}
