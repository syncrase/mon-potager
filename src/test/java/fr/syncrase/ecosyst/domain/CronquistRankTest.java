package fr.syncrase.ecosyst.domain;

import fr.syncrase.ecosyst.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CronquistRankTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CronquistRank.class);
        CronquistRank cronquistRank1 = new CronquistRank();
        cronquistRank1.setId(1L);
        CronquistRank cronquistRank2 = new CronquistRank();
        cronquistRank2.setId(cronquistRank1.getId());
        assertThat(cronquistRank1).isEqualTo(cronquistRank2);
        cronquistRank2.setId(2L);
        assertThat(cronquistRank1).isNotEqualTo(cronquistRank2);
        cronquistRank1.setId(null);
        assertThat(cronquistRank1).isNotEqualTo(cronquistRank2);
    }
}
