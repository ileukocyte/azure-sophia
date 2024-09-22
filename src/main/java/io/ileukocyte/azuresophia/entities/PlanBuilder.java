package io.ileukocyte.azuresophia.entities;

import java.time.OffsetDateTime;

/**
 * A builder class for a trip plan object
 */
public class PlanBuilder {
    private Plan.Priority priority = Plan.Priority.STANDARD;
    private String description;
    private OffsetDateTime datePlanned;
    private PlanAttachment attachment = null;
    private long id = -1;

    public PlanBuilder() {}

    /**
     * Builds a new plan based on an existing one
     *
     * @param plan the plan to modify
     */
    public PlanBuilder(Plan plan) {
        this.priority = plan.getPriority();
        this.description = plan.getDescription();
        this.datePlanned = plan.getDatePlanned();
        this.attachment = plan.getAttachment();
        this.id = plan.getId();
    }

    /**
     * Sets the plan's priority
     *
     * @param priority a priority to set
     *
     * @return the builder's instance
     */
    public PlanBuilder setPriority(Plan.Priority priority) {
        this.priority = priority;

        return this;
    }

    /**
     * Sets the plan's description
     *
     * @param description a description to set
     *
     * @return the builder's instance
     */
    public PlanBuilder setDescription(String description) {
        this.description = description;

        return this;
    }

    /**
     * Sets the plan's planned date
     *
     * @param datePlanned the planned date
     *
     * @return the builder's instance
     *
     * @see Plan#getDatePlanned()
     */
    public PlanBuilder setDatePlanned(OffsetDateTime datePlanned) {
        this.datePlanned = datePlanned;

        return this;
    }

    /**
     * Sets the plan's attachment
     *
     * @param attachment a plan attachment
     *
     * @return the builder's instance
     */
    public PlanBuilder setAttachment(PlanAttachment attachment) {
        this.attachment = attachment;

        return this;
    }

    /**
     * @return a finalized plan instance
     *
     * @throws InsufficientPlanDetailException in case no description or attachment is provided
     */
    public Plan build() {
        if (description == null && attachment == null) {
            throw new InsufficientPlanDetailException("Either a description or an attachment must be provided!");
        }

        return new Plan(priority, description, datePlanned, attachment, id < 0 ? System.currentTimeMillis() : id);
    }
}
