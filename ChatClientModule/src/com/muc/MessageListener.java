package com.muc;

/**
 * The interface Message listener.
 */
// Message listener interface class //
public interface MessageListener {
    /**
     * On message.
     *
     * @param fromLogin the from login
     * @param msgBody   the msg body
     */
    void onMessage(String fromLogin, String msgBody);
}
