package fr.syncrase.ecosyst.feature.add_plante.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import fr.syncrase.ecosyst.domain.*;
import fr.syncrase.ecosyst.domain.enumeration.CronquistTaxonomicRank;
import fr.syncrase.ecosyst.domain.enumeration.ReferenceType;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

class ScrapedPlantSerializerTest {

    @Test
    void serialize() throws JsonProcessingException {
        Plante plante = new Plante()
            .addNomsVernaculaires(new NomVernaculaire().nom("poireau"))
            .addReferences(new Reference().type(ReferenceType.SOURCE).url(new Url().url("https://fr.wikipedia.org/wiki/poireau")))
            .classification(new Object() {
                public Classification get() {
                    CronquistRank cronquistRank = new CronquistRank();
                    Classification classification1 = new Classification().cronquist(cronquistRank);
                    CronquistRank nextRank;
                    CronquistTaxonomicRank[] rangsDisponibles = CronquistTaxonomicRank.values();
                    for (CronquistTaxonomicRank hauteurDeRangEnCours : rangsDisponibles) {
                        cronquistRank.setRank(hauteurDeRangEnCours);
                        if (hauteurDeRangEnCours.equals(CronquistTaxonomicRank.ESPECE)) {
                            cronquistRank.setNom("Allium ampeloprasum var. porrum");
                        }
                        if (hauteurDeRangEnCours.equals(CronquistTaxonomicRank.GENRE)) {
                            cronquistRank.setNom("Allium");
                        }
                        if (hauteurDeRangEnCours.equals(CronquistTaxonomicRank.FAMILLE)) {
                            cronquistRank.setNom("Alliaceae");
                        }
                        if (hauteurDeRangEnCours.equals(CronquistTaxonomicRank.ORDRE)) {
                            cronquistRank.setNom("Liliales");
                        }
                        if (hauteurDeRangEnCours.equals(CronquistTaxonomicRank.SOUSCLASSE)) {
                            cronquistRank.setNom("Liliidae");
                        }
                        if (hauteurDeRangEnCours.equals(CronquistTaxonomicRank.CLASSE)) {
                            cronquistRank.setNom("Liliopsida");
                        }
                        if (hauteurDeRangEnCours.equals(CronquistTaxonomicRank.EMBRANCHEMENT)) {
                            cronquistRank.setNom("Magnoliophyta");
                        }
                        if (hauteurDeRangEnCours.equals(CronquistTaxonomicRank.SOUSREGNE)) {
                            cronquistRank.setNom("Tracheobionta");
                        }
                        if (hauteurDeRangEnCours.equals(CronquistTaxonomicRank.REGNE)) {
                            cronquistRank.setNom("Plantae");
                        }
                        nextRank = new CronquistRank();
                        cronquistRank.setChildren(Set.of(nextRank));
                        cronquistRank = nextRank;
                    }
                    return classification1;
                }
            }.get());
        @Nullable ScrapedPlant scrapedPlante = new ScrapedPlant(plante);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String serializedPlante = ow.writeValueAsString(scrapedPlante);
        String expected = "{\n" +
            "  \"plante\" : {\n" +
            "    \"id\" : null,\n" +
            "    \"nomsVernaculaires\" : [ {\n" +
            "      \"id\" : null,\n" +
            "      \"nom\" : \"poireau\",\n" +
            "      \"description\" : null\n" +
            "    } ],\n" +
            "    \"references\" : [ {\n" +
            "      \"id\" : null,\n" +
            "      \"type\" : \"SOURCE\",\n" +
            "      \"description\" : null,\n" +
            "      \"url\" : {\n" +
            "        \"id\" : null,\n" +
            "        \"url\" : \"https://fr.wikipedia.org/wiki/poireau\"\n" +
            "      }\n" +
            "    } ]\n" +
            "  },\n" +
            "  \"cronquistClassificationBranch\" : [ {\n" +
            "    \"id\" : null,\n" +
            "    \"nom\" : \"allium ampeloprasum var. porrum\",\n" +
            "    \"rank\" : \"ESPECE\",\n" +
            "    \"classification\" : {\n" +
            "      \"id\" : null\n" +
            "    }\n" +
            "  }, {\n" +
            "    \"id\" : null,\n" +
            "    \"nom\" : null,\n" +
            "    \"rank\" : \"SOUSSECTION\"\n" +
            "  }, {\n" +
            "    \"id\" : null,\n" +
            "    \"nom\" : null,\n" +
            "    \"rank\" : \"SECTION\"\n" +
            "  }, {\n" +
            "    \"id\" : null,\n" +
            "    \"nom\" : null,\n" +
            "    \"rank\" : \"SOUSGENRE\"\n" +
            "  }, {\n" +
            "    \"id\" : null,\n" +
            "    \"nom\" : \"allium\",\n" +
            "    \"rank\" : \"GENRE\"\n" +
            "  }, {\n" +
            "    \"id\" : null,\n" +
            "    \"nom\" : null,\n" +
            "    \"rank\" : \"SOUSTRIBU\"\n" +
            "  }, {\n" +
            "    \"id\" : null,\n" +
            "    \"nom\" : null,\n" +
            "    \"rank\" : \"TRIBU\"\n" +
            "  }, {\n" +
            "    \"id\" : null,\n" +
            "    \"nom\" : null,\n" +
            "    \"rank\" : \"SOUSFAMILLE\"\n" +
            "  }, {\n" +
            "    \"id\" : null,\n" +
            "    \"nom\" : \"alliaceae\",\n" +
            "    \"rank\" : \"FAMILLE\"\n" +
            "  }, {\n" +
            "    \"id\" : null,\n" +
            "    \"nom\" : null,\n" +
            "    \"rank\" : \"SUPERFAMILLE\"\n" +
            "  }, {\n" +
            "    \"id\" : null,\n" +
            "    \"nom\" : null,\n" +
            "    \"rank\" : \"MICROORDRE\"\n" +
            "  }, {\n" +
            "    \"id\" : null,\n" +
            "    \"nom\" : null,\n" +
            "    \"rank\" : \"INFRAORDRE\"\n" +
            "  }, {\n" +
            "    \"id\" : null,\n" +
            "    \"nom\" : null,\n" +
            "    \"rank\" : \"SOUSORDRE\"\n" +
            "  }, {\n" +
            "    \"id\" : null,\n" +
            "    \"nom\" : \"liliales\",\n" +
            "    \"rank\" : \"ORDRE\"\n" +
            "  }, {\n" +
            "    \"id\" : null,\n" +
            "    \"nom\" : null,\n" +
            "    \"rank\" : \"SUPERORDRE\"\n" +
            "  }, {\n" +
            "    \"id\" : null,\n" +
            "    \"nom\" : null,\n" +
            "    \"rank\" : \"INFRACLASSE\"\n" +
            "  }, {\n" +
            "    \"id\" : null,\n" +
            "    \"nom\" : \"liliidae\",\n" +
            "    \"rank\" : \"SOUSCLASSE\"\n" +
            "  }, {\n" +
            "    \"id\" : null,\n" +
            "    \"nom\" : \"liliopsida\",\n" +
            "    \"rank\" : \"CLASSE\"\n" +
            "  }, {\n" +
            "    \"id\" : null,\n" +
            "    \"nom\" : null,\n" +
            "    \"rank\" : \"SUPERCLASSE\"\n" +
            "  }, {\n" +
            "    \"id\" : null,\n" +
            "    \"nom\" : null,\n" +
            "    \"rank\" : \"MICROEMBRANCHEMENT\"\n" +
            "  }, {\n" +
            "    \"id\" : null,\n" +
            "    \"nom\" : null,\n" +
            "    \"rank\" : \"INFRAEMBRANCHEMENT\"\n" +
            "  }, {\n" +
            "    \"id\" : null,\n" +
            "    \"nom\" : null,\n" +
            "    \"rank\" : \"SOUSEMBRANCHEMENT\"\n" +
            "  }, {\n" +
            "    \"id\" : null,\n" +
            "    \"nom\" : \"magnoliophyta\",\n" +
            "    \"rank\" : \"EMBRANCHEMENT\"\n" +
            "  }, {\n" +
            "    \"id\" : null,\n" +
            "    \"nom\" : null,\n" +
            "    \"rank\" : \"SUPEREMBRANCHEMENT\"\n" +
            "  }, {\n" +
            "    \"id\" : null,\n" +
            "    \"nom\" : null,\n" +
            "    \"rank\" : \"INFRAREGNE\"\n" +
            "  }, {\n" +
            "    \"id\" : null,\n" +
            "    \"nom\" : null,\n" +
            "    \"rank\" : \"RAMEAU\"\n" +
            "  }, {\n" +
            "    \"id\" : null,\n" +
            "    \"nom\" : \"tracheobionta\",\n" +
            "    \"rank\" : \"SOUSREGNE\"\n" +
            "  }, {\n" +
            "    \"id\" : null,\n" +
            "    \"nom\" : \"plantae\",\n" +
            "    \"rank\" : \"REGNE\"\n" +
            "  }, {\n" +
            "    \"id\" : null,\n" +
            "    \"nom\" : null,\n" +
            "    \"rank\" : \"SUPERREGNE\"\n" +
            "  }, {\n" +
            "    \"id\" : null,\n" +
            "    \"nom\" : null,\n" +
            "    \"rank\" : \"SOUSDOMAINE\"\n" +
            "  }, {\n" +
            "    \"id\" : null,\n" +
            "    \"nom\" : null,\n" +
            "    \"rank\" : \"DOMAINE\"\n" +
            "  } ]\n" +
            "}";
        Assertions.assertEquals(expected, serializedPlante);

    }
}
