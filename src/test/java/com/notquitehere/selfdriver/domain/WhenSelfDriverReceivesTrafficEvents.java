package com.notquitehere.selfdriver.domain;

import org.junit.jupiter.api.Test;

import static com.notquitehere.selfdriver.domain.DriveMode.*;
import static com.notquitehere.selfdriver.domain.SensorEvent.simpleEvent;
import static com.notquitehere.selfdriver.domain.SensorType.TRAFFIC;
import static com.notquitehere.selfdriver.domain.SensorType.TRAFFIC_CLEAR;

public class WhenSelfDriverReceivesTrafficEvents extends SelfDriverTestBase {

    @Test
    public void trafficEventsCauseSpeedChanges() {
        SensorEvent[] events = {
            speedLimit(50),
            simpleEvent(TRAFFIC),
            simpleEvent(TRAFFIC_CLEAR)
        };

        verifyEventResponses(NORMAL, events, expectSpeeds(50, 40, 50));
        verifyEventResponses(SPORT, events, expectSpeeds(55, 50, 55));
        verifyEventResponses(SAFE, events, expectSpeeds(45, 30, 45));
    }

    @Test
    public void trafficEventIgnoredWhenInTraffic() {
        final int limit = 50;
        SensorEvent[] events = {
            speedLimit(limit),
            simpleEvent(TRAFFIC),
            simpleEvent(TRAFFIC)
        };

        verifyEventResponses(NORMAL, events, expectSpeeds(50, 40, 40));
        verifyEventResponses(SPORT, events, expectSpeeds(55, 50, 50));
        verifyEventResponses(SAFE, events, expectSpeeds(45, 30, 30));
    }

    @Test
    public void trafficClearEventIgnoredWhenNotInTraffic() {
        final int limit = 50;
        SensorEvent[] events = {
            speedLimit(limit),
            simpleEvent(TRAFFIC_CLEAR)
        };

        verifyEventResponses(NORMAL, events, expectSpeeds(50, 50));
        verifyEventResponses(SPORT, events, expectSpeeds(55, 55));
        verifyEventResponses(SAFE, events, expectSpeeds(45, 45));
    }

}
