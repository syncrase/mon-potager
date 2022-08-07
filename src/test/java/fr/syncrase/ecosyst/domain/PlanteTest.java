package fr.syncrase.ecosyst.domain;

import static org.assertj.core.api.Assertions.assertThat;

import fr.syncrase.ecosyst.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PlanteTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Plante.class);
        Plante plante1 = new Plante();
        plante1.setId(1L);
        Plante plante2 = new Plante();
        plante2.setId(plante1.getId());
        assertThat(plante1).isEqualTo(plante2);
        plante2.setId(2L);
        assertThat(plante1).isNotEqualTo(plante2);
        plante1.setId(null);
        assertThat(plante1).isNotEqualTo(plante2);
    }
}
