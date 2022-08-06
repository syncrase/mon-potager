package fr.syncrase.ecosyst.feature.add_plante.repository.exception;

public class MoreThanOneResultException extends Exception {
    public MoreThanOneResultException(String message) {
        super(message);
    }
}
