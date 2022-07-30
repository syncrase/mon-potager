package fr.syncrase.ecosyst.feature.add_plante.classification.entities;

import fr.syncrase.ecosyst.feature.add_plante.classification.entities.database.CronquistRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.enumeration.RankName;

import java.util.Set;

public interface ICronquistRank extends Cloneable {

    /**
     * CronquistRank factory
     *
     * @return
     */
    CronquistRank getCronquistRank();

    /*
    All about ranks names
     */
    RankName getRankName();

    void setRankName(RankName rankName);

    ICronquistRank rank(RankName rankName);

    /*
    All about IDs
     */
    Long getId();

    void setId(Long id);

    /*
    All about Urls
     */
    Set<IUrl> getIUrls();

    void setUrls(Set<IUrl> urls);

    ICronquistRank urls(Set<IUrl> urls);

    void addAllUrlsToCronquistRank(Set<IUrl> urls);

    ICronquistRank addUrl(IUrl url);

    void removeUrls();

    /*
    All about Names
     */
    Set<IClassificationNom> getNomsWrappers();

    void setNoms(Set<IClassificationNom> classificationNoms);

    ICronquistRank addNom(IClassificationNom nomFr);

    void addNameToCronquistRank(IClassificationNom existingNom);// TODO supprimer cette méthode. Doublon avec addNom

    void addAllNamesToCronquistRank(Set<IClassificationNom> noms);

    boolean doTheRankHasOneOfTheseNames(Set<IClassificationNom> noms);

    boolean doTheRankHasOneOfTheseNames(String... noms);

    ICronquistRank noms(Set<IClassificationNom> noms);

    void removeAllNames();

    boolean isRangSignificatif();

    boolean isRangDeLiaison();

    boolean isAnyNameHasAnId();

    /*
    All about the position in the classification (if allowed)
     */
    Set<ICronquistRank> getTaxons();

    ICronquistRank getParent();

    void removeTaxons();

    /*
    java Utils
     */
    public ICronquistRank clone();

    void removeAllNames(Set<Long> ranksIdToDelete);
}
