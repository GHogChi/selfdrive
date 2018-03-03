package com.xpmnt.selfdriver.domain;

import org.junit.jupiter.api.Test;

public class WhenSelfDriverReceivesTrafficEvents extends SelfDriverTestBase {

    @Test
    public void trafficEventsCauseSpeedChanges() {
        SensorEvent[] events = {
            speedLimit(50),
            SensorEvent.simpleEvent(SensorType.TRAFFIC),
            SensorEvent.simpleEvent(SensorType.TRAFFIC_CLEAR)
        };

        verifyEventResponses(DriveMode.NORMAL, events, expectSpeeds(50, 40, 50));
        verifyEventResponses(DriveMode.SPORT, events, expectSpeeds(55, 50, 55));
        verifyEventResponses(DriveMode.SAFE, events, expectSpeeds(45, 30, 45));
    }

    @Test
    public void trafficEventIgnoredWhenInTraffic() {
        final int limit = 50;
        SensorEvent[] events = {
            speedLimit(limit),
            SensorEvent.simpleEvent(SensorType.TRAFFIC),
            SensorEvent.simpleEvent(SensorType.TRAFFIC)
        };

        verifyEventResponses(DriveMode.NORMAL, events, expectSpeeds(50, 40, 40));
        verifyEventResponses(DriveMode.SPORT, events, expectSpeeds(55, 50, 50));
        verifyEventResponses(DriveMode.SAFE, events, expectSpeeds(45, 30, 30));
    }

    @Test
    public void trafficClearEventIgnoredWhenNotInTraffic() {
        final int limit = 50;
        SensorEvent[] events = {
            speedLimit(limit),
            SensorEvent.simpleEvent(SensorType.TRAFFIC_CLEAR)
        };

        verifyEventResponses(DriveMode.NORMAL, events, expectSpeeds(50, 50));
        verifyEventResponses(DriveMode.SPORT, events, expectSpeeds(55, 55));
        verifyEventResponses(DriveMode.SAFE, events, expectSpeeds(45, 45));
    }

}
