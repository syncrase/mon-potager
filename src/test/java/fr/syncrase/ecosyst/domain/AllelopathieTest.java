package fr.syncrase.ecosyst.domain;

import static org.assertj.core.api.Assertions.assertThat;

import fr.syncrase.ecosyst.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AllelopathieTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Allelopathie.class);
        Allelopathie allelopathie1 = new Allelopathie();
        allelopathie1.setId(1L);
        Allelopathie allelopathie2 = new Allelopathie();
        allelopathie2.setId(allelopathie1.getId());
        assertThat(allelopathie1).isEqualTo(allelopathie2);
        allelopathie2.setId(2L);
        assertThat(allelopathie1).isNotEqualTo(allelopathie2);
        allelopathie1.setId(null);
        assertThat(allelopathie1).isNotEqualTo(allelopathie2);
    }
}
