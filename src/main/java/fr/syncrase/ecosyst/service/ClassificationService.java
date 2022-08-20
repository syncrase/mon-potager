package fr.syncrase.ecosyst.service;

import fr.syncrase.ecosyst.domain.Classification;
import fr.syncrase.ecosyst.repository.ClassificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Service Implementation for managing {@link Classification}.
 */
@Service
@Transactional
public class ClassificationService {

    private final Logger log = LoggerFactory.getLogger(ClassificationService.class);

    private final ClassificationRepository classificationRepository;

    public ClassificationService(ClassificationRepository classificationRepository) {
        this.classificationRepository = classificationRepository;
    }

    /**
     * Save a classification.
     *
     * @param classification the entity to save.
     * @return the persisted entity.
     */
    public Classification save(Classification classification) {
        log.debug("Request to save Classification : {}", classification);
        return classificationRepository.save(classification);
    }

    /**
     * Update a classification.
     *
     * @param classification the entity to save.
     * @return the persisted entity.
     */
    public Classification update(Classification classification) {
        log.debug("Request to save Classification : {}", classification);
        // no save call needed as we have no fields that can be updated
        return classification;
    }

    /**
     * Partially update a classification.
     *
     * @param classification the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Classification> partialUpdate(Classification classification) {
        log.debug("Request to partially update Classification : {}", classification);

        return classificationRepository
            .findById(classification.getId())
            .map(existingClassification -> {
                return existingClassification;
            })// .map(classificationRepository::save)
        ;
    }

    /**
     * Get all the classifications.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Classification> findAll(Pageable pageable) {
        log.debug("Request to get all Classifications");
        return classificationRepository.findAll(pageable);
    }

    /**
     *  Get all the classifications where Cronquist is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Classification> findAllWhereCronquistIsNull() {
        log.debug("Request to get all classifications where Cronquist is null");
        return StreamSupport
            .stream(classificationRepository.findAll().spliterator(), false)
            .filter(classification -> classification.getCronquist() == null)
            .collect(Collectors.toList());
    }

    /**
     *  Get all the classifications where Apg is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Classification> findAllWhereApgIsNull() {
        log.debug("Request to get all classifications where Apg is null");
        return StreamSupport
            .stream(classificationRepository.findAll().spliterator(), false)
            .filter(classification -> classification.getApg() == null)
            .collect(Collectors.toList());
    }

    /**
     *  Get all the classifications where BenthamHooker is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Classification> findAllWhereBenthamHookerIsNull() {
        log.debug("Request to get all classifications where BenthamHooker is null");
        return StreamSupport
            .stream(classificationRepository.findAll().spliterator(), false)
            .filter(classification -> classification.getBenthamHooker() == null)
            .collect(Collectors.toList());
    }

    /**
     *  Get all the classifications where Wettstein is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Classification> findAllWhereWettsteinIsNull() {
        log.debug("Request to get all classifications where Wettstein is null");
        return StreamSupport
            .stream(classificationRepository.findAll().spliterator(), false)
            .filter(classification -> classification.getWettstein() == null)
            .collect(Collectors.toList());
    }

    /**
     *  Get all the classifications where Thorne is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Classification> findAllWhereThorneIsNull() {
        log.debug("Request to get all classifications where Thorne is null");
        return StreamSupport
            .stream(classificationRepository.findAll().spliterator(), false)
            .filter(classification -> classification.getThorne() == null)
            .collect(Collectors.toList());
    }

    /**
     *  Get all the classifications where Takhtajan is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Classification> findAllWhereTakhtajanIsNull() {
        log.debug("Request to get all classifications where Takhtajan is null");
        return StreamSupport
            .stream(classificationRepository.findAll().spliterator(), false)
            .filter(classification -> classification.getTakhtajan() == null)
            .collect(Collectors.toList());
    }

    /**
     *  Get all the classifications where Engler is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Classification> findAllWhereEnglerIsNull() {
        log.debug("Request to get all classifications where Engler is null");
        return StreamSupport
            .stream(classificationRepository.findAll().spliterator(), false)
            .filter(classification -> classification.getEngler() == null)
            .collect(Collectors.toList());
    }

    /**
     *  Get all the classifications where Candolle is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Classification> findAllWhereCandolleIsNull() {
        log.debug("Request to get all classifications where Candolle is null");
        return StreamSupport
            .stream(classificationRepository.findAll().spliterator(), false)
            .filter(classification -> classification.getCandolle() == null)
            .collect(Collectors.toList());
    }

    /**
     *  Get all the classifications where Dahlgren is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Classification> findAllWhereDahlgrenIsNull() {
        log.debug("Request to get all classifications where Dahlgren is null");
        return StreamSupport
            .stream(classificationRepository.findAll().spliterator(), false)
            .filter(classification -> classification.getDahlgren() == null)
            .collect(Collectors.toList());
    }

    /**
     * Get one classification by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Classification> findOne(Long id) {
        log.debug("Request to get Classification : {}", id);
        return classificationRepository.findById(id);
    }

    /**
     * Delete the classification by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Classification : {}", id);
        classificationRepository.deleteById(id);
    }
}
