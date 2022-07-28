package fr.syncrase.ecosyst.domain;

import static org.assertj.core.api.Assertions.assertThat;

import fr.syncrase.ecosyst.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CycleDeVieTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CycleDeVie.class);
        CycleDeVie cycleDeVie1 = new CycleDeVie();
        cycleDeVie1.setId(1L);
        CycleDeVie cycleDeVie2 = new CycleDeVie();
        cycleDeVie2.setId(cycleDeVie1.getId());
        assertThat(cycleDeVie1).isEqualTo(cycleDeVie2);
        cycleDeVie2.setId(2L);
        assertThat(cycleDeVie1).isNotEqualTo(cycleDeVie2);
        cycleDeVie1.setId(null);
        assertThat(cycleDeVie1).isNotEqualTo(cycleDeVie2);
    }
}
