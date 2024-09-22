package io.ileukocyte.azuresophia.database;

import io.ileukocyte.azuresophia.entities.SelfUser;

/**
 * A class containing references to both the security and personal data of a user object
 * <br />
 * <b>ONLY FOR TEMPORARY DB SIMULATION PURPOSES!</b>
 *
 * @see UserDatabase
 */
public class UserCredentials {
    private String key;
    private final SelfUser user;

    /**
     * @param key a security key to log into the service
     * @param user an object containing the data about the user profile to log into
     */
    public UserCredentials(String key, SelfUser user) {
        this.key = key;
        this.user = user;
    }

    /**
     * @return the user's security data
     */
    public String getKey() {
        return key;
    }

    /**
     * @return the data about the user profile to log into
     */
    public SelfUser getUser() {
        return user;
    }

    /**
     * @param key a new security key
     */
    public void setKey(String key) {
        this.key = key;
    }
}
