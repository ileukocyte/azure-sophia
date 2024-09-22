package io.ileukocyte.azuresophia.entities;

/**
 * An attachment that can be added to a plan
 *
 * @see Place
 * @see Tip
 * @see io.ileukocyte.azuresophia.database.AttachmentDatabase
 */
public interface PlanAttachment {
    /**
     * @return the title of a plan attachment
     */
    String getTitle();

    /**
     * @return the description of a plan attachment
     */
    default String getDescription() {
        return "No description provided";
    }

    /**
     * @return the ID of a plan attachment
     */
    String getId();
}