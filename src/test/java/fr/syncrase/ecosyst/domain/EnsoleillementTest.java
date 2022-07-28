package fr.syncrase.ecosyst.domain;

import static org.assertj.core.api.Assertions.assertThat;

import fr.syncrase.ecosyst.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EnsoleillementTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Ensoleillement.class);
        Ensoleillement ensoleillement1 = new Ensoleillement();
        ensoleillement1.setId(1L);
        Ensoleillement ensoleillement2 = new Ensoleillement();
        ensoleillement2.setId(ensoleillement1.getId());
        assertThat(ensoleillement1).isEqualTo(ensoleillement2);
        ensoleillement2.setId(2L);
        assertThat(ensoleillement1).isNotEqualTo(ensoleillement2);
        ensoleillement1.setId(null);
        assertThat(ensoleillement1).isNotEqualTo(ensoleillement2);
    }
}
