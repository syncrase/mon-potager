package fr.syncrase.ecosyst.domain;

import static org.assertj.core.api.Assertions.assertThat;

import fr.syncrase.ecosyst.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ThorneTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Thorne.class);
        Thorne thorne1 = new Thorne();
        thorne1.setId(1L);
        Thorne thorne2 = new Thorne();
        thorne2.setId(thorne1.getId());
        assertThat(thorne1).isEqualTo(thorne2);
        thorne2.setId(2L);
        assertThat(thorne1).isNotEqualTo(thorne2);
        thorne1.setId(null);
        assertThat(thorne1).isNotEqualTo(thorne2);
    }
}
