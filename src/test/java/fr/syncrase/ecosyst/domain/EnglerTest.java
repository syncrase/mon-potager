package fr.syncrase.ecosyst.domain;

import static org.assertj.core.api.Assertions.assertThat;

import fr.syncrase.ecosyst.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EnglerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Engler.class);
        Engler engler1 = new Engler();
        engler1.setId(1L);
        Engler engler2 = new Engler();
        engler2.setId(engler1.getId());
        assertThat(engler1).isEqualTo(engler2);
        engler2.setId(2L);
        assertThat(engler1).isNotEqualTo(engler2);
        engler1.setId(null);
        assertThat(engler1).isNotEqualTo(engler2);
    }
}
