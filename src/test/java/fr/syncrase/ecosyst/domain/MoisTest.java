package fr.syncrase.ecosyst.domain;

import static org.assertj.core.api.Assertions.assertThat;

import fr.syncrase.ecosyst.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MoisTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Mois.class);
        Mois mois1 = new Mois();
        mois1.setId(1L);
        Mois mois2 = new Mois();
        mois2.setId(mois1.getId());
        assertThat(mois1).isEqualTo(mois2);
        mois2.setId(2L);
        assertThat(mois1).isNotEqualTo(mois2);
        mois1.setId(null);
        assertThat(mois1).isNotEqualTo(mois2);
    }
}
