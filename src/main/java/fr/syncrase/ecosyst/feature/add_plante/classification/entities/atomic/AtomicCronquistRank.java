package fr.syncrase.ecosyst.feature.add_plante.classification.entities.atomic;

import fr.syncrase.ecosyst.feature.add_plante.classification.entities.IClassificationNom;
import fr.syncrase.ecosyst.feature.add_plante.classification.entities.ICronquistRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.entities.IUrl;
import fr.syncrase.ecosyst.feature.add_plante.classification.entities.database.CronquistRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.entities.mappers.ClassificationNomMapper;
import fr.syncrase.ecosyst.feature.add_plante.classification.entities.mappers.UrlMapper;
import fr.syncrase.ecosyst.feature.add_plante.classification.enumeration.RankName;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static fr.syncrase.ecosyst.feature.add_plante.classification.entities.database.CronquistRank.DEFAULT_NAME_FOR_CONNECTOR_RANK;

/**
 * TODO renommer en ScrappedCronquistRank ?
 * Un rang qui ne possède pas le rang parent ou les taxons
 */
public class AtomicCronquistRank implements ICronquistRank {
    private Long id;
    private RankName rank;
    private Set<IUrl> urls = new HashSet<>();
    private Set<IClassificationNom> noms = new HashSet<>();

    public AtomicCronquistRank(@NotNull ICronquistRank cronquistRank) {
        this.id = cronquistRank.getId();
        this.rank = cronquistRank.getRankName();
        this.urls = cronquistRank.getIUrls().stream().map(AtomicUrl::new).collect(Collectors.toSet());
        this.noms = cronquistRank.getNomsWrappers().stream().map(AtomicClassificationNom::new).collect(Collectors.toSet());
    }

    public AtomicCronquistRank() {

    }

    public AtomicCronquistRank(@NotNull CronquistRank cronquistRank2) {
        this.id = cronquistRank2.getId();
        this.rank = cronquistRank2.getRank();
        this.urls = cronquistRank2.getUrls().stream().map(AtomicUrl::new).collect(Collectors.toSet());
        this.noms = cronquistRank2.getNoms().stream().map(AtomicClassificationNom::new).collect(Collectors.toSet());

    }

    /**
     * Factory method For Default rank
     *
     * @param rankName Le rang que l'on souhaite créer
     * @return Un rang par défaut
     */
    public static ICronquistRank getDefaultRank(RankName rankName) {
        return new AtomicCronquistRank().rank(rankName).addNom(new AtomicClassificationNom().nomFr(DEFAULT_NAME_FOR_CONNECTOR_RANK));
    }

    @Contract("_ -> new")
    public static @NotNull ICronquistRank newRank(ICronquistRank rang) {
        return new AtomicCronquistRank(rang);
    }

    /**
     * Vérifie si tous les rangs sont intermédiaires
     *
     * @param ranks rangs dont on vérifie qu'ils sont tous intermédiaires
     * @return true si le rang en question est un rang intermédiaire
     */
    public static boolean isRangsIntermediaires(ICronquistRank @NotNull ... ranks) {
        if (ranks.length == 0) {
            return false;
        }
        Iterator<@NotNull ICronquistRank> cronquistRankIterator = Arrays.stream(ranks).iterator();
        while (cronquistRankIterator.hasNext()) {
            if (!cronquistRankIterator.next().isRangDeLiaison()) {
                return false;
            }
        }
        return true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ICronquistRank id(Long id) {
        this.setId(id);
        return this;
    }

    public RankName getRankName() {
        return rank;
    }

    public void setRankName(RankName rank) {
        this.rank = rank;
    }

    public Set<IClassificationNom> getNomsWrappers() {
        return noms;
    }

    @Override
    public void setNoms(Set<IClassificationNom> classificationNoms) {
        this.noms = classificationNoms;
    }

    public ICronquistRank addNom(IClassificationNom classificationNom) {
        this.noms.add(classificationNom);
        return this;
    }

    @Override
    public Set<ICronquistRank> getTaxons() {
        return null;
    }

    @Override
    public ICronquistRank noms(Set<IClassificationNom> classificationNoms) {
        this.setNoms(classificationNoms);
        return this;
    }

    @Override
    public ICronquistRank rank(RankName rankName) {
        this.setRankName(rankName);
        return this;
    }

    @Override
    public Set<IUrl> getIUrls() {
        return this.urls;
    }

    @Override
    public void setUrls(Set<IUrl> urls) {
        this.urls = urls;
    }

    @Override
    public ICronquistRank urls(Set<IUrl> urls) {
        this.setUrls(urls);
        return this;
    }

    @Override
    public ICronquistRank addUrl(IUrl url) {
        this.urls.add(url);
        return this;
    }

    /**
     * Ajoute toutes les urls au rang
     *
     * @param urls set des noms à ajouter
     */
    @Override
    public void addAllUrlsToCronquistRank(@NotNull Set<IUrl> urls) {
        for (IUrl url : urls) {
            this.addUrl(new AtomicUrl().url(url.getUrl()).id(url.getId()));// TODO l'instanciation est vraiment nécessaire ?
        }
    }

    @Override
    public void removeUrls() {
        this.urls.clear();
    }

    @Override
    public void removeAllNames() {
        this.noms.clear();
    }

    @Override
    public void removeTaxons() {
        throw new UnsupportedOperationException("Un rang atomique n'a pas la connaissance de ses taxons. C'est la classification qui possède cette information");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AtomicCronquistRank)) {
            return false;
        }
        return id != null && id.equals(((AtomicCronquistRank) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AtomicCronquistRank{" +
            "id=" + getId() +
            ", rank='" + getRankName() + "'" +
            ", names='" + getNomsWrappers().stream().map(n -> "{" + n.getId() + ":" + n.getNomFr() + "}").collect(Collectors.toList()) + "}";
    }

    /**
     * Vérifie si le rang est un rang intermédiaire
     *
     * @return true si le rang en question est un rang intermédiaire
     */
    public boolean isRangDeLiaison() {
        return hasThisName(DEFAULT_NAME_FOR_CONNECTOR_RANK);
    }

    /**
     * Vérifie si le rang est un rang significatif
     *
     * @return true si le rang en question est un rang significatif
     */
    public boolean isRangSignificatif() {
        return !this.isRangDeLiaison();
    }


    /**
     * Vérifie si le rang possède le nom passé en paramètre
     *
     * @param name nom que le rang a, ou n'a pas
     * @return true si le rang possède le nom
     */
    public boolean hasThisName(String name) {
        TreeSet<IClassificationNom> classificationNoms = AtomicClassificationNom.getAtomicClassificationNomTreeSet();
        classificationNoms.addAll(this.getNomsWrappers());
        return classificationNoms.contains(new AtomicClassificationNom().nomFr(name));
    }

    /**
     * Vérifie si le rang possède l'un des noms passés en paramètre
     *
     * @param names noms que le rang a, ou n'a pas
     * @return true si le rang possède le nom
     */
    @Override
    public boolean doTheRankHasOneOfTheseNames(Set<IClassificationNom> names) {
        TreeSet<IClassificationNom> classificationNoms = AtomicClassificationNom.getAtomicClassificationNomTreeSet();
        classificationNoms.addAll(names);
        for (IClassificationNom classificationNom : this.getNomsWrappers()) {
            if (classificationNoms.contains(classificationNom)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean doTheRankHasOneOfTheseNames(String @NotNull ... noms) {
        Set<IClassificationNom> nomSet = new HashSet<>();
        for (String nom : noms) {
            nomSet.add(new AtomicClassificationNom().nomFr(nom));
        }
        return doTheRankHasOneOfTheseNames(nomSet);
    }

    /**
     * Remplace le nom du rang intermédiaire par le nom connu ou ajoute le nom au set si c'est un rang taxonomique.<br>
     * Pour l'instant, ne gère que les nomsFr TODO gérer en plus le nom latin
     *
     * @param nomFr nomFr à ajouter au rang
     */
    public void addNameToCronquistRank(IClassificationNom nomFr) {
        if (this.isRangDeLiaison() && (nomFr.getNomFr() == null || nomFr.getNomLatin() == null)) {
            this.getNomsWrappers().removeIf(classificationNom -> classificationNom.getNomFr() == null);
        }
        this.addNom(new AtomicClassificationNom().nomFr(nomFr.getNomFr())
                        .id(nomFr.getId()));// TODO l'instanciation est vraiment nécessaire ?
    }

    /**
     * Ajoute tous les noms et les ids au rang
     *
     * @param names set des noms à ajouter
     */
    @Override
    public void addAllNamesToCronquistRank(@NotNull Set<IClassificationNom> names) {
        for (IClassificationNom classificationNom : names) {
            addNameToCronquistRank(classificationNom);
        }
    }

    public CronquistRank getCronquistRank() {
        CronquistRank rank = new CronquistRank()
            .id(this.id)
            .rank(this.rank)
            .urls(this.urls.stream().map(UrlMapper::get).collect(Collectors.toSet()))
            .noms(this.noms.stream().map(ClassificationNomMapper::get).collect(Collectors.toSet()));
        rank.makeConsistentAggregations();
        return rank;
    }

    @Override
    public ICronquistRank clone() {
        try {
            ICronquistRank clone = (ICronquistRank) super.clone();
            clone
                .urls(this.urls.stream().map(IUrl::clone).collect(Collectors.toSet()))
                .noms(this.noms.stream().map(IClassificationNom::clone)
                          .collect(Collectors.toSet()));
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public void removeAllNames(Set<Long> ranksIdToDelete) {
        this.noms.removeIf(classificationNom -> ranksIdToDelete.contains(classificationNom.getId()));
    }

    public boolean isAnyNameHasAnId() {
        return getNomsWrappers().stream().anyMatch(classificationNom -> classificationNom.getId() != null);
    }

    @Override
    public ICronquistRank getParent() {
        throw new UnsupportedOperationException("Un rang atomique n'a pas la connaissance de son parent. C'est la classification qui possède cette information");
    }

}
