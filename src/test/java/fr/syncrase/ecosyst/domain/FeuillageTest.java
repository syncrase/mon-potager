package fr.syncrase.ecosyst.domain;

import static org.assertj.core.api.Assertions.assertThat;

import fr.syncrase.ecosyst.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FeuillageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Feuillage.class);
        Feuillage feuillage1 = new Feuillage();
        feuillage1.setId(1L);
        Feuillage feuillage2 = new Feuillage();
        feuillage2.setId(feuillage1.getId());
        assertThat(feuillage1).isEqualTo(feuillage2);
        feuillage2.setId(2L);
        assertThat(feuillage1).isNotEqualTo(feuillage2);
        feuillage1.setId(null);
        assertThat(feuillage1).isNotEqualTo(feuillage2);
    }
}
