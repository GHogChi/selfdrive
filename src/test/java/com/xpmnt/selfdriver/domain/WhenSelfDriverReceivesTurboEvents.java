package com.xpmnt.selfdriver.domain;

import org.junit.jupiter.api.Test;

public class WhenSelfDriverReceivesTurboEvents extends SelfDriverTestBase {
    @Test
    public void speedIncreases() {
        SensorEvent[] events = {
            speedLimit(50),
            SensorEvent.simpleEvent(SensorType.EMERGENCY_TURBO)
        };

        verifyEventResponses(DriveMode.NORMAL, events, expectSpeeds(50, 70));
        verifyEventResponses(DriveMode.SPORT, events, expectSpeeds(55, 85));
        verifyEventResponses(DriveMode.SAFE, events, expectSpeeds(45, 55));
    }

    @Test
    public void turboIsIgnoredIfRoadIsSlippery() {
        SensorEvent[] events = {
            speedLimit(50),
            SensorEvent.simpleEvent(SensorType.SLIPPERY_ROAD),
            SensorEvent.simpleEvent(SensorType.EMERGENCY_TURBO)
        };

        verifyEventResponses(DriveMode.NORMAL, events, expectSpeeds(50, 35, 35));
        verifyEventResponses(DriveMode.SPORT, events, expectSpeeds(55, 40, 40));
        verifyEventResponses(DriveMode.SAFE, events, expectSpeeds(45, 30, 30));
    }

    /**
     * The requirement "9. A New Speed Limit Sign clears Emergency Turbo."
     * appears to mean (vacuously) that a lower speed limit "overrides
     * turbo" simply by virtue of setting the speed. This is the only option
     * provided for "clearing" turbo.
     *
     * Something feels wrong about this: an "emergency" should be a specific
     * event like encountering an obstacle on a one-lane road and passing it
     * in the oncoming traffic lane, and there should be a corresponding
     * "clear" event.
     */
    @Test
    public void turboCanOnlyBeAppliedOnce() {
        SensorEvent[] events = {
            speedLimit(50),
            SensorEvent.simpleEvent(SensorType.EMERGENCY_TURBO),
            speedLimit(50),
            SensorEvent.simpleEvent(SensorType.EMERGENCY_TURBO)
        };

        verifyEventResponses(
            DriveMode.NORMAL, events, expectSpeeds(50, 70, 50, 50));
        verifyEventResponses(
            DriveMode.SPORT, events, expectSpeeds(55, 85, 55, 55));
        verifyEventResponses(DriveMode.SAFE, events, expectSpeeds(45, 55, 45, 45));
    }


}
