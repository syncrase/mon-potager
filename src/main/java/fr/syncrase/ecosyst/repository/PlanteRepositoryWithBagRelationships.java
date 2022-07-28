package fr.syncrase.ecosyst.repository;

import fr.syncrase.ecosyst.domain.Plante;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface PlanteRepositoryWithBagRelationships {
    Optional<Plante> fetchBagRelationships(Optional<Plante> plante);

    List<Plante> fetchBagRelationships(List<Plante> plantes);

    Page<Plante> fetchBagRelationships(Page<Plante> plantes);
}
