package io.ileukocyte.azuresophia.entities;

import java.time.OffsetDateTime;
import java.util.*;

/**
 * A specific type of user object that provides methods
 * for modifying the user's personal data
 *
 * @see User
 */
public class SelfUser extends User {
    private final Deque<PlanAttachment> recentlyViewed;

    /**
     * @see User
     */
    public SelfUser(
            String name,
            String username,
            byte[] picture,
            String bio,
            Set<Plan> plans,
            Map<Place, OffsetDateTime> placesVisited
    ) {
        super(name, username, picture, bio, plans, placesVisited);

        recentlyViewed = new ArrayDeque<>();
    }

    /**
     * Sets the user's personal name
     *
     * @param name a personal name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets a username
     *
     * @param username a username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets a profile picture
     *
     * @param picture a profile picture
     */
    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    /**
     * Sets information about the user
     *
     * @param bio information about the user
     */
    public void setBio(String bio) {
        this.bio = bio;
    }

    /**
     * Adds a new plan
     *
     * @param plan a plan to add
     */
    public void addPlan(Plan plan) {
        plans.add(plan);
    }

    /**
     * Removes an existing plan
     *
     * @param plan a plan to remove
     */
    public void removePlan(Plan plan) {
        plans.remove(plan);
    }

    /**
     * Removes an existing plan by its ID
     *
     * @param id the ID of the plan to remove
     */
    public void removePlanById(long id) {
        plans.removeIf(plan -> plan.getId() == id);
    }

    /**
     * Adds a place visited by the user
     *
     * @param place the place visited
     * @param dateVisited the date when the place was visited
     */
    public void addPlaceVisited(Place place, OffsetDateTime dateVisited) {
        placesVisited.put(place, dateVisited);
    }

    /**
     * Adds place visited by the user
     *
     * @param place the place visited
     */
    public void addPlaceVisited(Place place) {
        addPlaceVisited(place, OffsetDateTime.now());
    }

    /**
     * Removes a place from those visited by the user
     *
     * @param place the place visited
     */
    public void removePlaceVisited(Place place) {
        placesVisited.remove(place);
    }

    /**
     * @return a collection of the places and tips opened by the user
     */
    public Deque<PlanAttachment> getRecentlyViewed() {
        return recentlyViewed;
    }

    /**
     * Adds a place or a tip to the user's history
     *
     * @param attachment the place or the tip to add to the history
     */
    public void addRecentlyViewed(PlanAttachment attachment) {
        recentlyViewed.remove(attachment);
        recentlyViewed.addFirst(attachment);
    }

    /**
     * Clears the user's view history
     */
    public void clearRecentlyViewed() {
        recentlyViewed.clear();
    }
}