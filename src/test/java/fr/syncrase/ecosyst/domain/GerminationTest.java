package fr.syncrase.ecosyst.domain;

import static org.assertj.core.api.Assertions.assertThat;

import fr.syncrase.ecosyst.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GerminationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Germination.class);
        Germination germination1 = new Germination();
        germination1.setId(1L);
        Germination germination2 = new Germination();
        germination2.setId(germination1.getId());
        assertThat(germination1).isEqualTo(germination2);
        germination2.setId(2L);
        assertThat(germination1).isNotEqualTo(germination2);
        germination1.setId(null);
        assertThat(germination1).isNotEqualTo(germination2);
    }
}
