package com.team341.server;

/**
 * Inherited by objects that wish to be notified when a new vision report is received
 */
public interface VisionUpdateReceiver {

    void updateReceived(VisionReport report);
}
