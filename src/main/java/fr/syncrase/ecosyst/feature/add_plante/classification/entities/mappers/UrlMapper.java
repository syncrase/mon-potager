package fr.syncrase.ecosyst.feature.add_plante.classification.entities.mappers;

import fr.syncrase.ecosyst.feature.add_plante.classification.entities.database.Url;
import fr.syncrase.ecosyst.feature.add_plante.classification.entities.IUrl;
import org.jetbrains.annotations.NotNull;

public class UrlMapper {

    //    public AtomicUrl get(@NotNull Url url) {
    //        return new AtomicUrl(url.getUrl(), url.getId());
    //    }

    public static Url get(@NotNull IUrl url) {
        return new Url(url.getUrl(), url.getId());
    }

}
