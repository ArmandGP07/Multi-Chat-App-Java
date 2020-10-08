package com.muc;

/**
 * The interface User status listener.
 */
// User status listener interface class //
public interface UserStatusListener {
    /**
     * Online.
     *
     * @param login the login
     */
    void online(String login);

    /**
     * Offline.
     *
     * @param login the login
     */
    void offline(String login);
}
