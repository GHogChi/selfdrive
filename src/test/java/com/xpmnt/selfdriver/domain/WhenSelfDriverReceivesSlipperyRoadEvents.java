package com.xpmnt.selfdriver.domain;

import org.junit.jupiter.api.Test;

public class WhenSelfDriverReceivesSlipperyRoadEvents extends
    SelfDriverTestBase {

    @Test
    public void slipperyRoadEventsCauseSpeedChanges() {
        SensorEvent[] events = {
            speedLimit(50),
            SensorEvent.simpleEvent(SensorType.SLIPPERY_ROAD),
            SensorEvent.simpleEvent(SensorType.SLIPPERY_ROAD_CLEAR)
        };
        verifyEventResponses(DriveMode.NORMAL, events, expectSpeeds(50, 35, 50));
        verifyEventResponses(DriveMode.SPORT, events, expectSpeeds(55, 40, 55));
        verifyEventResponses(DriveMode.SAFE, events, expectSpeeds(45, 30, 45));
    }

    @Test
    public void slipperyRoadEventsAreIgnoredWhenOnSlipperyRoad() {
        SensorEvent[] events = {
            speedLimit(50),
            SensorEvent.simpleEvent(SensorType.SLIPPERY_ROAD),
            SensorEvent.simpleEvent(SensorType.SLIPPERY_ROAD)
        };

        verifyEventResponses(DriveMode.NORMAL, events, expectSpeeds(50, 35, 35));
        verifyEventResponses(DriveMode.SPORT, events, expectSpeeds(55, 40, 40));
        verifyEventResponses(DriveMode.SAFE, events, expectSpeeds(45, 30, 30));
    }

    @Test
    public void slipperyRoadClearEventsAreIgnoredWhenNotOnSlipperyRoad() {
        SensorEvent[] events = {
            speedLimit(50),
            SensorEvent.simpleEvent(SensorType.SLIPPERY_ROAD_CLEAR)
        };

        verifyEventResponses(DriveMode.NORMAL, events, expectSpeeds(50, 50));
        verifyEventResponses(DriveMode.SPORT, events, expectSpeeds(55, 55));
        verifyEventResponses(DriveMode.SAFE, events, expectSpeeds(45, 45));
    }

}
