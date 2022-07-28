package fr.syncrase.ecosyst.domain;

import static org.assertj.core.api.Assertions.assertThat;

import fr.syncrase.ecosyst.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TemperatureTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Temperature.class);
        Temperature temperature1 = new Temperature();
        temperature1.setId(1L);
        Temperature temperature2 = new Temperature();
        temperature2.setId(temperature1.getId());
        assertThat(temperature1).isEqualTo(temperature2);
        temperature2.setId(2L);
        assertThat(temperature1).isNotEqualTo(temperature2);
        temperature1.setId(null);
        assertThat(temperature1).isNotEqualTo(temperature2);
    }
}
