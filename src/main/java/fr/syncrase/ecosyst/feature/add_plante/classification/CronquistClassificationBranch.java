package fr.syncrase.ecosyst.feature.add_plante.classification;

import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.domain.enumeration.CronquistTaxonomikRanks;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class CronquistClassificationBranch extends TreeSet<CronquistRank> implements Iterable<CronquistRank>, Set<CronquistRank> {

    /**
     * Deux rangs taxonomiques sont séparés par d'autres rangs dont on ne connait pas forcément le nom.<br>
     * Une valeur par défaut permet de lier ces deux rangs avec des rangs vides.<br>
     * Si ultérieurement ces rangs sont déterminés, les valeurs par défaut sont mises à jour
     */
    public static final String DEFAULT_NAME_FOR_CONNECTOR_RANK = null;

    /**
     * Liste de tous les rangs de la classification<br>
     * L'élément 0 est le rang le plus haut : le super règne
     */
    private final TreeSet<CronquistRank> classificationCronquist;

    /**
     * Construit une classification vierge
     */
    public CronquistClassificationBranch() {
        //this.classificationCronquist = new TreeSet<>(getCronquistRankComparator());
        this.classificationCronquist = new TreeSet<>();
        initDefaultValues();
    }

    /**
     * Construit une classification à partir d'un rang
     */
    public CronquistClassificationBranch(@NotNull CronquistRank cronquistRank) {
        this();
        CronquistRank currentRank = cronquistRank;
        while (currentRank != null) {
            add(currentRank);
            currentRank = currentRank.getParent();
        }
        this.clearTail();
    }

    public CronquistClassificationBranch(Set<CronquistRank> classificationCronquist) {
        this();
        this.addAll(classificationCronquist);
        this.clearTail();
    }

    /**
     * Each rank must own a parent (Invariant)
     */
    public void setConsistantParenthood() {
        Iterator<CronquistRank> iterator = classificationCronquist.descendingIterator();
        CronquistRank current, parent = null;
        while (iterator.hasNext()) {
            current = iterator.next();
            if (parent != null) {
                current.setParent(parent);
            }
            parent = current;
        }
    }

    //private static Comparator<CronquistRank> getCronquistRankComparator() {
    //    return Comparator.comparing(CronquistRank::getRank, (rang1, rang2) -> {
    //        if (rang1 == null && rang2 == null) {
    //            return 0;
    //        }
    //        if (rang1 == null) {
    //            return 1;
    //        }
    //        if (rang2 == null) {
    //            return -1;
    //        }
    //        return rang1.isHighestRankOf(rang2) ? 1 : rang1.isSameRankOf(rang2) ? 0 : -1;
    //    });

    //}

    @Deprecated
    @Nullable
    private CronquistRank getParent(@NotNull CronquistRank rank) throws InconsistentClassificationObject {
        if (rank.getRank() == null) {
            throw new InconsistentClassificationObject();
        }
        if (rank.getRank().getRangSuperieur() == null) {
            return null;
        }
        /* Cas où il n'y a pas de parent → quand c'est un domaine. Et dans ce cas null est déjà retournée
         * Un élément doit toujours être retourné
         */
        return classificationCronquist.stream().filter(cronquistRank -> cronquistRank.getRank().isSameRankOf(rank.getRank().getRangSuperieur())).findFirst().orElseThrow();

    }

    /**
     * Invariant : la taille maximum d'une classification de cronquist est de 34 rangs taxonomiques
     */
    private void initDefaultValues() {
        CronquistTaxonomikRanks[] rangsDisponibles = CronquistTaxonomikRanks.values();
        for (CronquistTaxonomikRanks hauteurDeRangEnCours : rangsDisponibles) {
            classificationCronquist.add(getDefaultRank(hauteurDeRangEnCours));
        }
    }

    /**
     * Retourne un rang vierge ne contenant que le <a href="https://fr.wikipedia.org/wiki/Rang_taxonomique">rang taxonomique</a>
     *
     * @param rangTaxonomique rang taxonomique du rang retourné
     * @return le rang dont le nom est le nom des rangs de liaison et dont le rang taxonomique est le rang passé en paramètre
     */
    private CronquistRank getDefaultRank(CronquistTaxonomikRanks rangTaxonomique) {
        // TODO déplacer dans la classe CronquistRank
        return new CronquistRank().rank(rangTaxonomique).nom(DEFAULT_NAME_FOR_CONNECTOR_RANK);
    }

    /**
     * La raison d'être des rangs de liaison (sans nom) est de lier deux rangs dont les noms sont connus.<br>
     * Cette méthode nettoie les rangs liaison inférieurs au dernier rang de la classification
     */
    public void clearTail() {
        Iterator<CronquistRank> iterator = classificationCronquist.iterator();
        while (iterator.hasNext()) {
            CronquistRank next = iterator.next();
            if (Objects.equals(next.getNom(), DEFAULT_NAME_FOR_CONNECTOR_RANK)) {
                iterator.remove();
            } else {
                break;
            }
        }
    }

    public static boolean isRangDeLiaison(@NotNull CronquistRank rangCible) {
        // TODO déplacer dans la classe CronquistRank
        return Objects.equals(rangCible.getNom(), DEFAULT_NAME_FOR_CONNECTOR_RANK);
    }

    private void removeTaxon(@NotNull CronquistTaxonomikRanks nomDuRangEnCours) {
        classificationCronquist.removeIf(cronquistRank -> cronquistRank.getRank().equals(nomDuRangEnCours));
    }

    public CronquistRank getRang(CronquistTaxonomikRanks rang) {
        if (rang != null) {
            CronquistRank ceiling = classificationCronquist.ceiling(new CronquistRank().rank(rang));
            if (ceiling == null || !ceiling.getRank().isSameRankOf(rang)) {
                return null;
            }
            return ceiling;
        } else {
            return null;
        }
    }

    public TreeSet<CronquistRank> getClassificationSet() {
        return classificationCronquist;
    }

    public CronquistRank getLowestRank() {
        return classificationCronquist.first();
    }

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return "CronquistClassificationBranch{" + "classificationCronquistMap=" + classificationCronquist + '}';
    }

    @Override
    public int size() {
        return classificationCronquist.size();
    }

    @Override
    public boolean isEmpty() {
        return classificationCronquist.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return classificationCronquist.contains(o);
    }

    @NotNull
    @Override
    public Iterator<CronquistRank> iterator() {
        return classificationCronquist.iterator();
    }

    @Contract(value = " -> new", pure = true)
    @NotNull
    @Override
    public Object @NotNull [] toArray() {
        return new Object[0];
    }

    @Contract(pure = true)
    @NotNull
    @Override
    public <T> T @Nullable [] toArray(@NotNull T[] a) {
        return null;
    }

    @Override
    public void forEach(Consumer<? super CronquistRank> action) {
        classificationCronquist.forEach(action);
    }

    @Override
    public Spliterator<CronquistRank> spliterator() {
        return classificationCronquist.spliterator();
    }

    @Override
    public boolean add(@NotNull CronquistRank cronquistRank) {
        CronquistRank thisCorrespondingRank = this.getRang(cronquistRank.getRank());
        boolean isListChanged = !Objects.equals(thisCorrespondingRank.getNom(), cronquistRank.getNom()) || !Objects.equals(thisCorrespondingRank.getId(), cronquistRank.getId());
        thisCorrespondingRank.setNom(cronquistRank.getNom());
        thisCorrespondingRank.setId(cronquistRank.getId());
        return isListChanged;
    }

    @Override
    public boolean remove(Object o) {
        return classificationCronquist.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return classificationCronquist.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends CronquistRank> c) {
        return c.stream().map(this::add).mapToInt(b -> b ? 1 : 0).sum() > 0;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return classificationCronquist.retainAll(c);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return classificationCronquist.removeAll(c);
    }

    @Override
    public void clear() {
        classificationCronquist.clear();
    }

    @Override
    public <T> T[] toArray(IntFunction<T[]> generator) {
        return super.toArray(generator);
    }

    @Override
    public boolean removeIf(Predicate<? super CronquistRank> filter) {
        return super.removeIf(filter);
    }

    @Override
    public Stream<CronquistRank> stream() {
        return super.stream();
    }

    @Override
    public Stream<CronquistRank> parallelStream() {
        return super.parallelStream();
    }
}
