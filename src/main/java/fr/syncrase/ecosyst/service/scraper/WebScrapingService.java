package fr.syncrase.ecosyst.service.scraper;

import fr.syncrase.ecosyst.domain.NomVernaculaire;
import fr.syncrase.ecosyst.domain.Plante;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service for scrap {@link Plante} on the internet
 */
@Service
public class WebScrapingService {

    private final Logger log = LoggerFactory.getLogger(WebScrapingService.class);

    public Plante scrapPlantData(String name) {
        return new Plante().addNomsVernaculaires(new NomVernaculaire().nom(name));
    }
}
