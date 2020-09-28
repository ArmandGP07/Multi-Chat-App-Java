package com.muc;

// User status listener interface class //
public interface UserStatusListener {
    public void online(String login);
    public void offline(String login);
}
