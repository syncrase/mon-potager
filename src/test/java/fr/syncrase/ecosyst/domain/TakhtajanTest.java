package fr.syncrase.ecosyst.domain;

import static org.assertj.core.api.Assertions.assertThat;

import fr.syncrase.ecosyst.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TakhtajanTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Takhtajan.class);
        Takhtajan takhtajan1 = new Takhtajan();
        takhtajan1.setId(1L);
        Takhtajan takhtajan2 = new Takhtajan();
        takhtajan2.setId(takhtajan1.getId());
        assertThat(takhtajan1).isEqualTo(takhtajan2);
        takhtajan2.setId(2L);
        assertThat(takhtajan1).isNotEqualTo(takhtajan2);
        takhtajan1.setId(null);
        assertThat(takhtajan1).isNotEqualTo(takhtajan2);
    }
}
