package fr.syncrase.ecosyst.feature.add_plante.repository;

import fr.syncrase.ecosyst.domain.*;
import fr.syncrase.ecosyst.service.*;
import fr.syncrase.ecosyst.service.criteria.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlanteReader {

    private CronquistRankQueryService cronquistRankQueryService;

    private NomVernaculaireQueryService nomVernaculaireQueryService;

    private PlanteQueryService planteQueryService;

    private ClassificationQueryService classificationQueryService;

    private ReferenceQueryService referenceQueryService;

    @Autowired
    public void setReferenceQueryService(ReferenceQueryService referenceQueryService) {
        this.referenceQueryService = referenceQueryService;
    }

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

    /**
     * Load all lists contained in the plante object
     *
     * @param plante will receive all list content
     * @return the plante with loaded lists content
     */
    public Plante eagerLoad(@NotNull Plante plante) {

        LongFilter planteIdFilter = new LongFilter();
        planteIdFilter.setEquals(plante.getId());

        NomVernaculaireCriteria nomCriteria = new NomVernaculaireCriteria();
        nomCriteria.setPlantesId(planteIdFilter);
        List<NomVernaculaire> nomsVernaculaires = nomVernaculaireQueryService.findByCriteria(nomCriteria);
        if (nomsVernaculaires.size() != 0) {
            plante.setNomsVernaculaires(new HashSet<>(nomsVernaculaires));
        } else {
            plante.setNomsVernaculaires(null);
        }

        ReferenceCriteria referenceCriteria = new ReferenceCriteria();
        referenceCriteria.setPlantesId(planteIdFilter);
        List<Reference> referencesByCriteria = referenceQueryService.findByCriteria(referenceCriteria);
        if (referencesByCriteria.size() != 0) {
            plante.setReferences(new HashSet<>(referencesByCriteria));
        } else {
            plante.setReferences(null);
        }
        return plante;
    }
}
