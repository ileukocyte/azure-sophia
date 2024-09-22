package io.ileukocyte.azuresophia.entities;

import java.util.List;

/**
 * A class containing data about a sight or a place
 */
public class Place implements PlanAttachment {
    private final String name;
    private final String id;
    private final String description;
    private final Location location;
    private final List<String> pictures;

    /**
     * @param name the name
     * @param id the ID
     * @param description the description
     * @param location the location
     * @param pictures a list of URLs to pictures of the place
     */
    public Place(String name, String id, String description, Location location, List<String> pictures) {
        this.name = name;
        this.id = id;
        this.description = description;
        this.location = location;
        this.pictures = pictures;
    }

    /**
     * @return the name of the place
     */
    @Override
    public String getTitle() {
        return name;
    }

    /**
     * @return the description of the place
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * @return information about the place's location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * @return a list of URLs to pictures of the place
     */
    public List<String> getPictures() {
        return pictures;
    }

    /**
     * @return the ID of the place
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * An object containing information about location of a place
     *
     * @param humanizedAddress the location's humanized address
     * @param latitude         the latitude
     * @param longitude        the longitude
     */
    public record Location(String humanizedAddress, double latitude, double longitude) {}
}
