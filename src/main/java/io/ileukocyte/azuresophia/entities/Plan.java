package io.ileukocyte.azuresophia.entities;

import java.time.OffsetDateTime;

/**
 * A trip plan for a user to schedule
 */
public class Plan {
    private final Priority priority;
    private final String description;
    private final OffsetDateTime dateAdded;
    private final OffsetDateTime datePlanned;
    private final PlanAttachment attachment;
    private final long id;

    /**
     * @param priority the priority
     * @param description the description
     * @param datePlanned the planned date of fulfillment
     * @param attachment the attachment (i. e., a Place or a Tip)
     * @param id the ID
     */
    protected Plan(Priority priority, String description, OffsetDateTime datePlanned, PlanAttachment attachment, long id) {
        this.priority = priority;
        this.description = description;
        this.dateAdded = OffsetDateTime.now();
        this.datePlanned = datePlanned;
        this.attachment = attachment;
        this.id = id;
    }

    /**
     * @return the plan's priority
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * @return the plan's content
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the date the plan has been added
     */
    public OffsetDateTime getDateAdded() {
        return dateAdded;
    }

    /**
     * @return the date by which the plan is scheduled to be fulfilled
     */
    public OffsetDateTime getDatePlanned() {
        return datePlanned;
    }

    /**
     * @return the plan's attachment
     */
    public PlanAttachment getAttachment() {
        return attachment;
    }

    /**
     * @return the plan's ID
     */
    public long getId() {
        return id;
    }

    /**
     * An enum of degrees of a plan's priority
     */
    public enum Priority {
        HIGH,
        STANDARD,
        LOW;

        @Override
        public String toString() {
            var string = super.toString();

            return !string.isEmpty() ?
                    Character.toUpperCase(string.charAt(0)) + string.substring(1).toLowerCase() :
                    string;
        }
    }
}
