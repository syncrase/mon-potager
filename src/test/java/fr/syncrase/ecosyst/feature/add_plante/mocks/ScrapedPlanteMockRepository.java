package fr.syncrase.ecosyst.feature.add_plante.mocks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.syncrase.ecosyst.feature.add_plante.models.ScrapedPlant;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ScrapedPlanteMockRepository {

    PORRUM("{ \"plante\" : { \"id\" : null, \"nomsVernaculaires\" : [ { \"id\" : null, \"nom\" : \"poireau\", \"description\" : null } ], \"references\" : [ { \"id\" : null, \"type\" : \"IMAGE\", \"description\" : null, \"url\" : { \"id\" : null, \"url\" : \"https://i.pinimg.com/originals/5d/b9/65/5db965e06918f104a719a5fcc31eb5d2.jpg\" } }, { \"id\" : null, \"type\" : \"SOURCE\", \"description\" : null, \"url\" : { \"id\" : null, \"url\" : \"https://fr.wikipedia.org/wiki/poireau\" } } ] }, \"cronquistClassificationBranch\" : [ { \"id\" : null, \"nom\" : null, \"rank\" : \"SOUSFORME\" }, { \"id\" : null, \"nom\" : null, \"rank\" : \"FORME\" }, { \"id\" : null, \"nom\" : null, \"rank\" : \"SOUSVARIETE\" }, { \"id\" : null, \"nom\" : null, \"rank\" : \"VARIETE\" }, { \"id\" : null, \"nom\" : null, \"rank\" : \"SOUSESPECE\" }, { \"id\" : null, \"nom\" : \"allium ampeloprasum var. porrum\", \"rank\" : \"ESPECE\" }, { \"id\" : null, \"nom\" : null, \"rank\" : \"SOUSSECTION\" }, { \"id\" : null, \"nom\" : null, \"rank\" : \"SECTION\" }, { \"id\" : null, \"nom\" : null, \"rank\" : \"SOUSGENRE\" }, { \"id\" : null, \"nom\" : \"allium\", \"rank\" : \"GENRE\" }, { \"id\" : null, \"nom\" : null, \"rank\" : \"SOUSTRIBU\" }, { \"id\" : null, \"nom\" : null, \"rank\" : \"TRIBU\" }, { \"id\" : null, \"nom\" : null, \"rank\" : \"SOUSFAMILLE\" }, { \"id\" : null, \"nom\" : \"alliaceae\", \"rank\" : \"FAMILLE\" }, { \"id\" : null, \"nom\" : null, \"rank\" : \"SUPERFAMILLE\" }, { \"id\" : null, \"nom\" : null, \"rank\" : \"MICROORDRE\" }, { \"id\" : null, \"nom\" : null, \"rank\" : \"INFRAORDRE\" }, { \"id\" : null, \"nom\" : null, \"rank\" : \"SOUSORDRE\" }, { \"id\" : null, \"nom\" : \"liliales\", \"rank\" : \"ORDRE\" }, { \"id\" : null, \"nom\" : null, \"rank\" : \"SUPERORDRE\" }, { \"id\" : null, \"nom\" : null, \"rank\" : \"INFRACLASSE\" }, { \"id\" : null, \"nom\" : \"liliidae\", \"rank\" : \"SOUSCLASSE\" }, { \"id\" : null, \"nom\" : \"liliopsida\", \"rank\" : \"CLASSE\" }, { \"id\" : null, \"nom\" : null, \"rank\" : \"SUPERCLASSE\" }, { \"id\" : null, \"nom\" : null, \"rank\" : \"MICROEMBRANCHEMENT\" }, { \"id\" : null, \"nom\" : null, \"rank\" : \"INFRAEMBRANCHEMENT\" }, { \"id\" : null, \"nom\" : null, \"rank\" : \"SOUSEMBRANCHEMENT\" }, { \"id\" : null, \"nom\" : \"magnoliophyta\", \"rank\" : \"EMBRANCHEMENT\" }, { \"id\" : null, \"nom\" : null, \"rank\" : \"SUPEREMBRANCHEMENT\" }, { \"id\" : null, \"nom\" : null, \"rank\" : \"INFRAREGNE\" }, { \"id\" : null, \"nom\" : null, \"rank\" : \"RAMEAU\" }, { \"id\" : null, \"nom\" : \"tracheobionta\", \"rank\" : \"SOUSREGNE\" }, { \"id\" : null, \"nom\" : \"plantae\", \"rank\" : \"REGNE\" }, { \"id\" : null, \"nom\" : null, \"rank\" : \"SUPERREGNE\" }, { \"id\" : null, \"nom\" : null, \"rank\" : \"SOUSDOMAINE\" }, { \"id\" : null, \"nom\" : null, \"rank\" : \"DOMAINE\" } ]}");


    private final Logger log = LoggerFactory.getLogger(ScrapedPlanteMockRepository.class);
    private final String json;

    ScrapedPlanteMockRepository(String json) {
        this.json = json;
    }

    @NotNull
    public ScrapedPlant getPlante() {
        ScrapedPlant scrapedPlant;
        try {
            scrapedPlant = new ObjectMapper().readValue(this.json, ScrapedPlant.class);
        } catch (JsonProcessingException e) {
            log.error("Unable to build a scrapedPlant from the JSON");
            throw new RuntimeException(e);
        }
        return scrapedPlant;
    }
}
