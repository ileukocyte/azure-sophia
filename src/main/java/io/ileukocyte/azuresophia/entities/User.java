package io.ileukocyte.azuresophia.entities;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;

/**
 * A class containing non-modifiable data about an application user
 */
public class User {
    protected String name;
    protected String username;
    protected byte[] picture;
    protected String bio;
    protected Set<Plan> plans;
    protected Map<Place, OffsetDateTime> placesVisited;

    /**
     * @param name the personal name
     * @param username the username
     * @param picture the profile picture
     * @param bio the biography
     * @param plans the trip plans
     * @param placesVisited the places visited
     */
    public User(
            String name,
            String username,
            byte[] picture,
            String bio,
            Set<Plan> plans,
            Map<Place, OffsetDateTime> placesVisited
    ) {
        this.name = name;
        this.username = username;
        this.picture = picture;
        this.bio = bio;
        this.plans = plans;
        this.placesVisited = placesVisited;
    }

    /**
     * @return the user's personal name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return bytes of the user's profile picture
     */
    public byte[] getPicture() {
        return picture;
    }

    /**
     * @return the information set by the user about themselves
     */
    public String getBio() {
        return bio;
    }

    /**
     * @return a set of plans the user has currently added
     */
    public Set<Plan> getPlans() {
        return plans;
    }

    /**
     * @return the places the user has visited so far
     */
    public Map<Place, OffsetDateTime> getPlacesVisited() {
        return placesVisited;
    }
}
