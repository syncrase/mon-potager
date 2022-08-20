package fr.syncrase.ecosyst.domain;

import fr.syncrase.ecosyst.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClassificationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Classification.class);
        Classification classification1 = new Classification();
        classification1.setId(1L);
        Classification classification2 = new Classification();
        classification2.setId(classification1.getId());
        assertThat(classification1).isEqualTo(classification2);
        classification2.setId(2L);
        assertThat(classification1).isNotEqualTo(classification2);
        classification1.setId(null);
        assertThat(classification1).isNotEqualTo(classification2);
    }
}
