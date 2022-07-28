package fr.syncrase.ecosyst.domain;

import static org.assertj.core.api.Assertions.assertThat;

import fr.syncrase.ecosyst.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StrateTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Strate.class);
        Strate strate1 = new Strate();
        strate1.setId(1L);
        Strate strate2 = new Strate();
        strate2.setId(strate1.getId());
        assertThat(strate1).isEqualTo(strate2);
        strate2.setId(2L);
        assertThat(strate1).isNotEqualTo(strate2);
        strate1.setId(null);
        assertThat(strate1).isNotEqualTo(strate2);
    }
}
