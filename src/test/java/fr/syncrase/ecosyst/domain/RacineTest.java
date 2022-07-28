package fr.syncrase.ecosyst.domain;

import static org.assertj.core.api.Assertions.assertThat;

import fr.syncrase.ecosyst.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RacineTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Racine.class);
        Racine racine1 = new Racine();
        racine1.setId(1L);
        Racine racine2 = new Racine();
        racine2.setId(racine1.getId());
        assertThat(racine1).isEqualTo(racine2);
        racine2.setId(2L);
        assertThat(racine1).isNotEqualTo(racine2);
        racine1.setId(null);
        assertThat(racine1).isNotEqualTo(racine2);
    }
}
