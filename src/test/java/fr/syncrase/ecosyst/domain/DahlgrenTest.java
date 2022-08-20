package fr.syncrase.ecosyst.domain;

import fr.syncrase.ecosyst.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DahlgrenTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Dahlgren.class);
        Dahlgren dahlgren1 = new Dahlgren();
        dahlgren1.setId(1L);
        Dahlgren dahlgren2 = new Dahlgren();
        dahlgren2.setId(dahlgren1.getId());
        assertThat(dahlgren1).isEqualTo(dahlgren2);
        dahlgren2.setId(2L);
        assertThat(dahlgren1).isNotEqualTo(dahlgren2);
        dahlgren1.setId(null);
        assertThat(dahlgren1).isNotEqualTo(dahlgren2);
    }
}
