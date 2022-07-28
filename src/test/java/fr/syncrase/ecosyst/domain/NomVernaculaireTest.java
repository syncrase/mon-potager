package fr.syncrase.ecosyst.domain;

import static org.assertj.core.api.Assertions.assertThat;

import fr.syncrase.ecosyst.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NomVernaculaireTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(NomVernaculaire.class);
        NomVernaculaire nomVernaculaire1 = new NomVernaculaire();
        nomVernaculaire1.setId(1L);
        NomVernaculaire nomVernaculaire2 = new NomVernaculaire();
        nomVernaculaire2.setId(nomVernaculaire1.getId());
        assertThat(nomVernaculaire1).isEqualTo(nomVernaculaire2);
        nomVernaculaire2.setId(2L);
        assertThat(nomVernaculaire1).isNotEqualTo(nomVernaculaire2);
        nomVernaculaire1.setId(null);
        assertThat(nomVernaculaire1).isNotEqualTo(nomVernaculaire2);
    }
}
