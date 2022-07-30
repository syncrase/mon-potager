package fr.syncrase.ecosyst.feature.add_plante.classification.entities;

import fr.syncrase.ecosyst.feature.add_plante.classification.entities.atomic.AtomicUrl;
import fr.syncrase.ecosyst.feature.add_plante.classification.entities.database.Url;

public interface IUrl extends Cloneable {
    Long getId();

    void setId(Long id);

    public AtomicUrl id(Long id);

    String getUrl();

    Url newUrl();

    public IUrl clone();
}
