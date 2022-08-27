package fr.syncrase.ecosyst.feature.add_plante.classification;

import fr.syncrase.ecosyst.domain.CronquistRank;
import fr.syncrase.ecosyst.domain.enumeration.CronquistTaxonomicRank;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class CronquistClassificationBranch extends TreeSet<CronquistRank> implements Iterable<CronquistRank>, Set<CronquistRank> {

    private final Logger log = LoggerFactory.getLogger(CronquistClassificationBranch.class);

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
        if (!cronquistRank.getRank().equals(CronquistTaxonomicRank.DOMAINE)) {
            // Go up
            while (currentRank != null) {
                add(currentRank);
                currentRank = currentRank.getParent();
            }
        } else {
            // Go down
            while (currentRank != null) {
                add(currentRank);
                Optional<CronquistRank> rankOptional = currentRank.getChildren().stream().filter(cronquistRank1 -> cronquistRank1.getRank() != null).findFirst();
                currentRank = rankOptional.orElse(null);
            }

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
        if (!iterator.hasNext()) {
            log.error("Cannot define an consistent parenthood on an empty classification");
        }
        CronquistRank current, parent;
        current = iterator.next();
        parent = current;
        while (iterator.hasNext()) {
            current = iterator.next();
            parent.getChildren().add(current);
            current.setParent(parent);
            parent = current;
        }
    }

    public void clearRank(CronquistTaxonomicRank ctr) {
        this.getRang(ctr).setNom(null);// TODO supprimer cette méthode => utiliser remove à la place
    }

    /**
     * Invariant : la taille maximum d'une classification de cronquist est de 34 rangs taxonomiques
     */
    private void initDefaultValues() {
        CronquistTaxonomicRank[] rangsDisponibles = CronquistTaxonomicRank.values();
        for (CronquistTaxonomicRank hauteurDeRangEnCours : rangsDisponibles) {
            classificationCronquist.add(getDefaultRank(hauteurDeRangEnCours));
        }
    }

    /**
     * Retourne un rang vierge ne contenant que le <a href="https://fr.wikipedia.org/wiki/Rang_taxonomique">rang taxonomique</a>
     *
     * @param rangTaxonomique rang taxonomique du rang retourné
     * @return le rang dont le nom est le nom des rangs de liaison et dont le rang taxonomique est le rang passé en paramètre
     */
    private CronquistRank getDefaultRank(CronquistTaxonomicRank rangTaxonomique) {
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

    public CronquistRank getRang(CronquistTaxonomicRank rang) {
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
        if (thisCorrespondingRank == null) {
            throw new RuntimeException("Fail to get rank " + cronquistRank);
        }
        boolean isListChanged = !Objects.equals(thisCorrespondingRank.getNom(), cronquistRank.getNom()) || !Objects.equals(thisCorrespondingRank.getId(), cronquistRank.getId());
        if (cronquistRank.getNom() != null) {
            thisCorrespondingRank.setNom(cronquistRank.getNom().toLowerCase());
        }
        thisCorrespondingRank.setId(cronquistRank.getId());
        return isListChanged;
    }

    @Override
    public boolean remove(Object o) {
        CronquistRank thisCorrespondingRank;
        if (o instanceof CronquistRank) {
            CronquistRank cr = (CronquistRank) o;
            thisCorrespondingRank = this.getRang(cr.getRank());
        } else if (o instanceof CronquistTaxonomicRank) {
            CronquistTaxonomicRank ctr = (CronquistTaxonomicRank) o;
            thisCorrespondingRank = this.getRang(ctr);
        } else {
            throw new RuntimeException("Trying to clear a rank with an unhandled parameter type");
        }
        thisCorrespondingRank.setNom(null);
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
    public boolean removeIf(Predicate<? super CronquistRank> filter) {
        return classificationCronquist.removeIf(filter);
    }

    @Override
    public Stream<CronquistRank> stream() {
        return classificationCronquist.stream();
    }

    @Override
    public Stream<CronquistRank> parallelStream() {
        return classificationCronquist.parallelStream();
    }

    @Override
    public @NotNull SortedSet<CronquistRank> subSet(CronquistRank fromElement, CronquistRank toElement) {
        return classificationCronquist.subSet(fromElement, toElement);
    }

    @Override
    public @NotNull NavigableSet<CronquistRank> subSet(CronquistRank fromElement, boolean fromInclusive, CronquistRank toElement, boolean toInclusive) {
        return classificationCronquist.subSet(fromElement, fromInclusive, toElement, toInclusive);
    }
}
