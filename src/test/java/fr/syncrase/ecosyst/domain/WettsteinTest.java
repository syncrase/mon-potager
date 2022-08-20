package fr.syncrase.ecosyst.domain;

import static org.assertj.core.api.Assertions.assertThat;

import fr.syncrase.ecosyst.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WettsteinTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Wettstein.class);
        Wettstein wettstein1 = new Wettstein();
        wettstein1.setId(1L);
        Wettstein wettstein2 = new Wettstein();
        wettstein2.setId(wettstein1.getId());
        assertThat(wettstein1).isEqualTo(wettstein2);
        wettstein2.setId(2L);
        assertThat(wettstein1).isNotEqualTo(wettstein2);
        wettstein1.setId(null);
        assertThat(wettstein1).isNotEqualTo(wettstein2);
    }
}
