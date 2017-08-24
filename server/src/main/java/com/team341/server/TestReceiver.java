package com.team341.server;

/**
 * Created by josh on 8/23/17.
 */

public class TestReceiver implements VisionUpdateReceiver {
    @Override
    public void updateReceived(VisionReport report) {
        if (report.getTargets().size() < 1) {
            System.out.println("No report available");
        } else {
            System.out.println("Width: " + report.getTargets().get(0).getWidth());
        }
    }
}
