package io.ileukocyte.azuresophia.database;

import io.ileukocyte.azuresophia.entities.SelfUser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.security.auth.login.LoginException;

/**
 * A class containing methods to operate user profiles present in the program
 * <br />
 * <b>ONLY FOR TEMPORARY DB SIMULATION PURPOSES!</b>
 *
 * @see UserCredentials
 */
public class UserDatabase {
    /**
     * A simulation of a database of all the signed-up users
     */
    public static Map<String, UserCredentials> ACTIVE_USERS = new LinkedHashMap<>();

    /**
     * A user profile that is currently logged into
     */
    public static SelfUser CURRENT_USER = null;

    /**
     * Creates a new user profile
     *
     * @param username a chosen username
     * @param key a security key
     * @param name a person's name
     *
     * @return the instance of the currently logged-in profile
     */
    public static SelfUser addUser(String username, String key, String name) {
        if (ACTIVE_USERS.containsKey(username)) {
            throw new IllegalArgumentException("The provided username is already taken!");
        }

        var user = new SelfUser(name, username, null, null, new HashSet<>(), new HashMap<>());

        ACTIVE_USERS.put(username, new UserCredentials(key, user));

        CURRENT_USER = user;

        return CURRENT_USER;
    }

    /**
     * Logs into an existing user profile
     *
     * @param username username
     * @param key the security key
     *
     * @return the instance of the currently logged-in profile
     *
     * @throws LoginException in case the provided credentials are wrong
     */
    public static SelfUser logIn(String username, String key) throws LoginException {
        var credentials = ACTIVE_USERS.get(username);

        if (credentials == null) {
            throw new LoginException("Either the provided username or password is wrong!");
        }

        if (!credentials.getKey().equals(key)) {
            throw new LoginException("Either the provided username or password is wrong!");
        }

        CURRENT_USER = credentials.getUser();

        return CURRENT_USER;
    }

    /**
     * A convenience method in order to log out of the current user profile
     */
    public static void logOut() {
        CURRENT_USER = null;
    }
}
