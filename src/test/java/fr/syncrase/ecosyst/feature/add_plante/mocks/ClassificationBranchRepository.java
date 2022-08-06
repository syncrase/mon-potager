package fr.syncrase.ecosyst.feature.add_plante.mocks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.CronquistClassificationBranch;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TreeSet;

public enum ClassificationBranchRepository {

    ALLIUM("[ { \"id\" : null, \"rank\" : \"GENRE\", \"nom\" : \"Allium\", \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"SOUSTRIBU\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"TRIBU\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"SOUSFAMILLE\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"FAMILLE\", \"nom\" : \"Liliaceae\", \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"SUPERFAMILLE\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"MICROORDRE\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"INFRAORDRE\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"SOUSORDRE\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"ORDRE\", \"nom\" : \"Liliales\", \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"SUPERORDRE\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"INFRACLASSE\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"SOUSCLASSE\", \"nom\" : \"Liliidae\", \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"CLASSE\", \"nom\" : \"Liliopsida\", \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"SUPERCLASSE\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"MICROEMBRANCHEMENT\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"INFRAEMBRANCHEMENT\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"SOUSEMBRANCHEMENT\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"EMBRANCHEMENT\", \"nom\" : \"Magnoliophyta\", \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"SUPEREMBRANCHEMENT\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"INFRAREGNE\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"RAMEAU\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"SOUSREGNE\", \"nom\" : \"Tracheobionta\", \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"REGNE\", \"nom\" : \"Plantae\", \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"SUPERREGNE\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"SOUSDOMAINE\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"DOMAINE\", \"nom\" : null, \"children\" : [ ], \"parent\" : null} ]"),
    ALDROVANDA("[ { \"id\" : null, \"rank\" : \"GENRE\", \"nom\" : \"Aldrovanda\", \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"SOUSTRIBU\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"TRIBU\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"SOUSFAMILLE\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"FAMILLE\", \"nom\" : \"Droseraceae\", \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"SUPERFAMILLE\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"MICROORDRE\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"INFRAORDRE\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"SOUSORDRE\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"ORDRE\", \"nom\" : \"Nepenthales\", \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"SUPERORDRE\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"INFRACLASSE\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"SOUSCLASSE\", \"nom\" : \"Dilleniidae\", \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"CLASSE\", \"nom\" : \"Magnoliopsida\", \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"SUPERCLASSE\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"MICROEMBRANCHEMENT\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"INFRAEMBRANCHEMENT\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"SOUSEMBRANCHEMENT\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"EMBRANCHEMENT\", \"nom\" : \"Magnoliophyta\", \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"SUPEREMBRANCHEMENT\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"INFRAREGNE\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"RAMEAU\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"SOUSREGNE\", \"nom\" : \"Tracheobionta\", \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"REGNE\", \"nom\" : \"Plantae\", \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"SUPERREGNE\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"SOUSDOMAINE\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : null, \"rank\" : \"DOMAINE\", \"nom\" : null, \"children\" : [ ], \"parent\" : null} ]"),
    MAGNOLIOPHYTA("[ { \"id\" : 1019, \"rank\" : \"EMBRANCHEMENT\", \"nom\" : \"Magnoliophyta\", \"children\" : [ ], \"parent\" : null}, { \"id\" : 1020, \"rank\" : \"SUPEREMBRANCHEMENT\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : 1021, \"rank\" : \"INFRAREGNE\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : 1022, \"rank\" : \"RAMEAU\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : 1023, \"rank\" : \"SOUSREGNE\", \"nom\" : \"Tracheobionta\", \"children\" : [ ], \"parent\" : null}, { \"id\" : 1024, \"rank\" : \"REGNE\", \"nom\" : \"Plantae\", \"children\" : [ ], \"parent\" : null}, { \"id\" : 1025, \"rank\" : \"SUPERREGNE\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : 1026, \"rank\" : \"SOUSDOMAINE\", \"nom\" : null, \"children\" : [ ], \"parent\" : null}, { \"id\" : 1027, \"rank\" : \"DOMAINE\", \"nom\" : null, \"children\" : [ ], \"parent\" : null} ]"),
    aze2("");


    private final Logger log = LoggerFactory.getLogger(ClassificationBranchRepository.class);
    private final String json;

    ClassificationBranchRepository(String json) {
        this.json = json;
    }

    @NotNull
    public CronquistClassificationBranch getClassification() {
        TreeSet<CronquistRank> classification = null;
        try {
            classification = new ObjectMapper().readValue(this.json, new TypeReference<TreeSet<CronquistRank>>() {
            });
        } catch (JsonProcessingException e) {
            log.error("Unable to build a classification from the JSON");
            throw new RuntimeException(e);
        }
        return new CronquistClassificationBranch(classification);
    }
}
