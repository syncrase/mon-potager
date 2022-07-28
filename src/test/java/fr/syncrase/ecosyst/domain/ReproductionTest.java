package fr.syncrase.ecosyst.domain;

import static org.assertj.core.api.Assertions.assertThat;

import fr.syncrase.ecosyst.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReproductionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Reproduction.class);
        Reproduction reproduction1 = new Reproduction();
        reproduction1.setId(1L);
        Reproduction reproduction2 = new Reproduction();
        reproduction2.setId(reproduction1.getId());
        assertThat(reproduction1).isEqualTo(reproduction2);
        reproduction2.setId(2L);
        assertThat(reproduction1).isNotEqualTo(reproduction2);
        reproduction1.setId(null);
        assertThat(reproduction1).isNotEqualTo(reproduction2);
    }
}
