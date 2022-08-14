package fr.syncrase.ecosyst.feature.add_plante.classification;

import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.feature.add_plante.mocks.ClassificationBranchMockRepository;
import fr.syncrase.ecosyst.feature.add_plante.repository.CronquistWriter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class CronquistClassificationBranchTest {

    @Test
    void constructorTrueCopyTest() {
        CronquistClassificationBranch c1 = ClassificationBranchMockRepository.ARJONA.getClassification();
        CronquistClassificationBranch c2 = new CronquistClassificationBranch(c1);
        Assertions.assertEquals(c1.size(), c2.size(), "La copie de la liste doit être de la même taille que le liste initiale");

        Iterator<CronquistRank> iterator1 = c1.iterator();
        Iterator<CronquistRank> iterator2 = c2.iterator();
        while (iterator1.hasNext() && iterator2.hasNext()) {
            CronquistRank rank1 = iterator1.next();
            CronquistRank rank2 = iterator2.next();
            Assertions.assertEquals(rank1.getRank(), rank2.getRank(), "Les deux rangs doivent se trouver au même niveau de l'iterator");
            Assertions.assertEquals(rank1.getNom(), rank2.getNom(), "Les deux rangs doivent avoir le même nom");
            Assertions.assertEquals(rank1.getId(), rank2.getId(), "Les deux rangs doivent avoir le même ID");

            assertNotSame(rank1, rank2, "Les deux rangs ne doivent pas posséder la même référence");
        }

    }

    @Test
    void setConsistantParenthood() {
    }

    @Test
    void clearTail() {
    }

    @Test
    void isRangDeLiaison() {
    }

    @Test
    void getRang() {
    }

    @Test
    void getClassificationSet() {
    }

    @Test
    void getLowestRank() {
    }

    @Test
    void add() {
    }

    @Test
    void remove() {
    }

    @Test
    void addAll() {
    }
}
