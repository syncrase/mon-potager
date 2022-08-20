package fr.syncrase.ecosyst.domain;

import static org.assertj.core.api.Assertions.assertThat;

import fr.syncrase.ecosyst.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReferenceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Reference.class);
        Reference reference1 = new Reference();
        reference1.setId(1L);
        Reference reference2 = new Reference();
        reference2.setId(reference1.getId());
        assertThat(reference1).isEqualTo(reference2);
        reference2.setId(2L);
        assertThat(reference1).isNotEqualTo(reference2);
        reference1.setId(null);
        assertThat(reference1).isNotEqualTo(reference2);
    }
}
