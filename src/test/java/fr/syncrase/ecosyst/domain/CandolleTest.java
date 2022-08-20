package fr.syncrase.ecosyst.domain;

import fr.syncrase.ecosyst.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CandolleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Candolle.class);
        Candolle candolle1 = new Candolle();
        candolle1.setId(1L);
        Candolle candolle2 = new Candolle();
        candolle2.setId(candolle1.getId());
        assertThat(candolle1).isEqualTo(candolle2);
        candolle2.setId(2L);
        assertThat(candolle1).isNotEqualTo(candolle2);
        candolle1.setId(null);
        assertThat(candolle1).isNotEqualTo(candolle2);
    }
}
