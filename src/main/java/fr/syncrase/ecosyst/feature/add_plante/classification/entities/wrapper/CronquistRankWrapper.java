package fr.syncrase.ecosyst.feature.add_plante.classification.entities.wrapper;

import fr.syncrase.ecosyst.feature.add_plante.classification.entities.atomic.AtomicClassificationNom;
import fr.syncrase.ecosyst.feature.add_plante.classification.entities.database.Url;
import fr.syncrase.ecosyst.feature.add_plante.classification.entities.IClassificationNom;
import fr.syncrase.ecosyst.feature.add_plante.classification.entities.ICronquistRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.entities.IUrl;
import fr.syncrase.ecosyst.feature.add_plante.classification.entities.atomic.AtomicCronquistRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.entities.atomic.AtomicUrl;
import fr.syncrase.ecosyst.feature.add_plante.classification.entities.database.ClassificationNom;
import fr.syncrase.ecosyst.feature.add_plante.classification.entities.database.CronquistRank;
import fr.syncrase.ecosyst.feature.add_plante.classification.entities.mappers.ClassificationNomMapper;
import fr.syncrase.ecosyst.feature.add_plante.classification.entities.mappers.UrlMapper;
import fr.syncrase.ecosyst.feature.add_plante.classification.enumeration.RankName;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static fr.syncrase.ecosyst.feature.add_plante.classification.entities.database.CronquistRank.DEFAULT_NAME_FOR_CONNECTOR_RANK;

/**
 * Wrap le rang cronquist stocké en base de données.
 * Se construit à partir d'un objet existant en base de donnée uniquement
 */
public class CronquistRankWrapper implements ICronquistRank {

    private final CronquistRank cronquistRank;

    public CronquistRankWrapper(CronquistRank cronquistRank) {
        this.cronquistRank = cronquistRank;
    }

    public CronquistRankWrapper(@NotNull ICronquistRank rank) {
        this.cronquistRank = rank.getCronquistRank();
    }

    @Override
    public CronquistRank getCronquistRank() {
        return this.cronquistRank;
    }

    @Override
    public RankName getRankName() {
        return this.cronquistRank.getRank();
    }

    @Override
    public void setRankName(RankName rankName) {
        this.cronquistRank.setRank(rankName);
    }

    @Override
    public ICronquistRank rank(RankName rankName) {
        setRankName(rankName);
        return this;
    }

    @Override
    public Long getId() {
        return this.cronquistRank.getId();
    }

    @Override
    public void setId(Long id) {
        this.cronquistRank.setId(id);
    }

    @Override
    public Set<IUrl> getIUrls() {
        return this.cronquistRank.getUrls().stream().map(AtomicUrl::new).collect(Collectors.toSet());
    }

    @Override
    public ICronquistRank urls(@NotNull Set<IUrl> urls) {
        this.cronquistRank.setUrls(urls.stream().map(UrlMapper::get).collect(Collectors.toSet()));
        return this;
    }

    @Override
    public void addAllUrlsToCronquistRank(@NotNull Set<IUrl> urls) {
        for (IUrl url : urls) {
            this.addUrl(new AtomicUrl().url(url.getUrl()).id(url.getId())); // TODO check si elle existe déjà
        }
    }

    @Override
    public ICronquistRank addNom(IClassificationNom nom) {
        this.cronquistRank.getNoms().add(ClassificationNomMapper.get(nom));
        return this;
    }

    @Override
    public ICronquistRank noms(@NotNull Set<IClassificationNom> noms) {
        this.cronquistRank.setNoms(noms.stream().map(ClassificationNomMapper::get).collect(Collectors.toSet()));
        return this;
    }

    public Set<ClassificationNom> getNoms() {// TODO rename to getClassificationNoms
        return this.cronquistRank.getNoms();
    }

    @Override
    public String toString() {
        return "CronquistRankWrapper{" +
            "cronquistRank=" + cronquistRank +
            '}';
    }

    @Override
    public Set<IClassificationNom> getNomsWrappers() {
        return this.cronquistRank.getNoms().stream().map(ClassificationNomWrapper::new).collect(Collectors.toSet());
    }

    @Override
    public void setNoms(@NotNull Set<IClassificationNom> classificationNoms) {
        this.cronquistRank.setNoms(
            classificationNoms.stream()
                .map(IClassificationNom::getClassificationNom)
                .collect(Collectors.toSet())
                                  );
    }

    @Override
    public void removeAllNames(Set<Long> ranksIdToDelete) {
        this.cronquistRank.getNoms().removeIf(classificationNom -> ranksIdToDelete.contains(classificationNom.getId()));
    }

    @Override
    public void removeAllNames() {
        this.cronquistRank.getNoms().clear();
    }

    @Override
    public Set<ICronquistRank> getTaxons() {
        return this.cronquistRank.getChildren().stream().map(AtomicCronquistRank::new).collect(Collectors.toSet());
    }

    @Override
    public boolean isRangSignificatif() {
        return !this.isRangDeLiaison();
    }

    @Override
    public boolean isRangDeLiaison() {
        return hasThisName(DEFAULT_NAME_FOR_CONNECTOR_RANK);
    }

    /**
     * Vérifie si le rang possède le nom passé en paramètre
     *
     * @param name nom que le rang a, ou n'a pas
     * @return true si le rang possède le nom
     */
    public boolean hasThisName(String name) {// TODO default method
        TreeSet<IClassificationNom> classificationNoms = AtomicClassificationNom.getAtomicClassificationNomTreeSet();
        classificationNoms.addAll(this.getNomsWrappers());
        return classificationNoms.contains(new AtomicClassificationNom().nomFr(name));
    }

    @Override
    public void addAllNamesToCronquistRank(@NotNull Set<IClassificationNom> noms) {
        for (IClassificationNom classificationNom : noms) {
            addNameToCronquistRank(classificationNom);
        }
    }

    @Override
    public boolean isAnyNameHasAnId() {
        return getNomsWrappers().stream().anyMatch(classificationNom -> classificationNom.getId() != null);// TODO default final ?
    }

    @Override
    public ICronquistRank getParent() {
        CronquistRank parent = this.cronquistRank.getParent();
        return parent == null ? null : new CronquistRankWrapper(parent);
    }

    @Override
    public ICronquistRank clone() {
        try {
            ICronquistRank clone = (ICronquistRank) super.clone();
            clone
                .urls(this.getIUrls().stream().map(IUrl::clone).collect(Collectors.toSet()))
                .noms(this.getNomsWrappers().stream().map(IClassificationNom::clone)
                          .collect(Collectors.toSet()));

            return new CronquistRankWrapper(clone);
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public boolean doTheRankHasOneOfTheseNames(Set<IClassificationNom> noms) {
        TreeSet<IClassificationNom> classificationNoms = AtomicClassificationNom.getAtomicClassificationNomTreeSet();
        classificationNoms.addAll(noms);
        for (IClassificationNom classificationNom : this.getNomsWrappers()) {
            if (classificationNoms.contains(classificationNom)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean doTheRankHasOneOfTheseNames(String @NotNull ... noms) {
        Set<IClassificationNom> nomSet = new HashSet<>();// TODO default class ?
        for (String nom : noms) {
            nomSet.add(new ClassificationNomWrapper().nomFr(nom));
        }
        return doTheRankHasOneOfTheseNames(nomSet);
    }

    @Override
    public void addNameToCronquistRank(IClassificationNom nom) {
        // TODO default method ?
        if (this.isRangDeLiaison()) {
            this.getNomsWrappers().removeIf(classificationNom -> classificationNom.getNomFr() == null);
        }
        this.addNom(new AtomicClassificationNom().nomFr(nom.getNomFr())
                        .id(nom.getId()));
    }

    @Override
    public ICronquistRank addUrl(IUrl newAtomicUrl) {
        this.cronquistRank.getUrls().add(UrlMapper.get(newAtomicUrl));
        return this;
    }

    @Override
    public void removeUrls() {
        this.cronquistRank.getUrls().clear();
    }

    @Override
    public void removeTaxons() {
        this.cronquistRank.getChildren().clear();
    }

    public Set<Url> getUrls() {
        return this.cronquistRank.getUrls();
    }

    @Override
    public void setUrls(Set<IUrl> urls) {

    }
}
