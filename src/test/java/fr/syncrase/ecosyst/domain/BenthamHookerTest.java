package fr.syncrase.ecosyst.domain;

import fr.syncrase.ecosyst.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BenthamHookerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BenthamHooker.class);
        BenthamHooker benthamHooker1 = new BenthamHooker();
        benthamHooker1.setId(1L);
        BenthamHooker benthamHooker2 = new BenthamHooker();
        benthamHooker2.setId(benthamHooker1.getId());
        assertThat(benthamHooker1).isEqualTo(benthamHooker2);
        benthamHooker2.setId(2L);
        assertThat(benthamHooker1).isNotEqualTo(benthamHooker2);
        benthamHooker1.setId(null);
        assertThat(benthamHooker1).isNotEqualTo(benthamHooker2);
    }
}
