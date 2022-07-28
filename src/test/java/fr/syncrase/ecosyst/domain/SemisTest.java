package fr.syncrase.ecosyst.domain;

import static org.assertj.core.api.Assertions.assertThat;

import fr.syncrase.ecosyst.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SemisTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Semis.class);
        Semis semis1 = new Semis();
        semis1.setId(1L);
        Semis semis2 = new Semis();
        semis2.setId(semis1.getId());
        assertThat(semis1).isEqualTo(semis2);
        semis2.setId(2L);
        assertThat(semis1).isNotEqualTo(semis2);
        semis1.setId(null);
        assertThat(semis1).isNotEqualTo(semis2);
    }
}
