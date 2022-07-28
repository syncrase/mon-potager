package fr.syncrase.ecosyst.domain;

import static org.assertj.core.api.Assertions.assertThat;

import fr.syncrase.ecosyst.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RessemblanceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Ressemblance.class);
        Ressemblance ressemblance1 = new Ressemblance();
        ressemblance1.setId(1L);
        Ressemblance ressemblance2 = new Ressemblance();
        ressemblance2.setId(ressemblance1.getId());
        assertThat(ressemblance1).isEqualTo(ressemblance2);
        ressemblance2.setId(2L);
        assertThat(ressemblance1).isNotEqualTo(ressemblance2);
        ressemblance1.setId(null);
        assertThat(ressemblance1).isNotEqualTo(ressemblance2);
    }
}
