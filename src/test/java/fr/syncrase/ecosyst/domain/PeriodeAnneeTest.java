package fr.syncrase.ecosyst.domain;

import static org.assertj.core.api.Assertions.assertThat;

import fr.syncrase.ecosyst.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PeriodeAnneeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PeriodeAnnee.class);
        PeriodeAnnee periodeAnnee1 = new PeriodeAnnee();
        periodeAnnee1.setId(1L);
        PeriodeAnnee periodeAnnee2 = new PeriodeAnnee();
        periodeAnnee2.setId(periodeAnnee1.getId());
        assertThat(periodeAnnee1).isEqualTo(periodeAnnee2);
        periodeAnnee2.setId(2L);
        assertThat(periodeAnnee1).isNotEqualTo(periodeAnnee2);
        periodeAnnee1.setId(null);
        assertThat(periodeAnnee1).isNotEqualTo(periodeAnnee2);
    }
}
