package com.team341.frc2017.vision;

/**
 * Inherited by objects that wish to be notified when a new vision report is received
 */
public interface VisionUpdateReceiver {

    void updateReceived(VisionReport report);
}
