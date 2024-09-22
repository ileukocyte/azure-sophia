package io.ileukocyte.azuresophia.entities;

/**
 * A tip containing some useful information for traveling
 */
public class Tip implements PlanAttachment {
    private final String title;
    private final String description;
    private final String id;
    private final String image;
    private final Place place;

    /**
     * @param title the tip's title
     * @param description the description
     * @param id the tip's ID
     * @param image the URL to the tip's picture
     * @param place a place the tip regards
     */
    public Tip(String title, String description, String id, String image, Place place) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.image = image;
        this.place = place;
    }

    /**
     * @return the tip's title
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * @return the tip's content
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * @return the ID of the tip
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * @return the URL of the tip's preview image
     */
    public String getImage() {
        return image;
    }

    /**
     * @return the place the tip regards
     */
    public Place getPlace() {
        return place;
    }
}
