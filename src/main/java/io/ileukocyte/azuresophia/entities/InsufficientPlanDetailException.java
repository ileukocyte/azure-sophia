package io.ileukocyte.azuresophia.entities;

/**
 * A custom exception used in case a plan cannot be created due to
 * an insufficient amount of data provided
 */
public class InsufficientPlanDetailException extends RuntimeException {
    /**
     * Instantiates a new Insufficient plan detail exception.
     */
    public InsufficientPlanDetailException() {
        super();
    }

    /**
     * Instantiates a new Insufficient plan detail exception.
     *
     * @param message the message
     */
    public InsufficientPlanDetailException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Insufficient plan detail exception.
     *
     * @param cause the cause
     */
    public InsufficientPlanDetailException(Throwable cause) {
        super(cause);
    }

    /**
     * Instantiates a new Insufficient plan detail exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public InsufficientPlanDetailException(String message, Throwable cause) {
        super(message, cause);
    }
}
