package fr.syncrase.ecosyst.domain;

import static org.assertj.core.api.Assertions.assertThat;

import fr.syncrase.ecosyst.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SolTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Sol.class);
        Sol sol1 = new Sol();
        sol1.setId(1L);
        Sol sol2 = new Sol();
        sol2.setId(sol1.getId());
        assertThat(sol1).isEqualTo(sol2);
        sol2.setId(2L);
        assertThat(sol1).isNotEqualTo(sol2);
        sol1.setId(null);
        assertThat(sol1).isNotEqualTo(sol2);
    }
}
