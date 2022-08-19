package fr.syncrase.ecosyst.feature.add_plante.consistency.conflict_and_merge;

import fr.syncrase.ecosyst.feature.add_plante.scraper.WebScrapingService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class ServiceTestConfiguration {

    @Bean
    @Primary
    public WebScrapingService nameService() {
        return Mockito.mock(WebScrapingService.class);
    }
}
