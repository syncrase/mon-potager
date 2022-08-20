package fr.syncrase.ecosyst.domain;

import fr.syncrase.ecosyst.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class APGTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(APG.class);
        APG aPG1 = new APG();
        aPG1.setId(1L);
        APG aPG2 = new APG();
        aPG2.setId(aPG1.getId());
        assertThat(aPG1).isEqualTo(aPG2);
        aPG2.setId(2L);
        assertThat(aPG1).isNotEqualTo(aPG2);
        aPG1.setId(null);
        assertThat(aPG1).isNotEqualTo(aPG2);
    }
}
