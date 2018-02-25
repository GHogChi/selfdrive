package com.notquitehere.selfdriver.domain;

import org.junit.jupiter.api.Test;

import static com.notquitehere.selfdriver.domain.DriveMode.NORMAL;
import static com.notquitehere.selfdriver.domain.DriveMode.SAFE;
import static com.notquitehere.selfdriver.domain.DriveMode.SPORT;
import static com.notquitehere.selfdriver.domain.SensorEvent.simpleEvent;
import static com.notquitehere.selfdriver.domain.SensorType.SLIPPERY_ROAD;
import static com.notquitehere.selfdriver.domain.SensorType.SLIPPERY_ROAD_CLEAR;

public class WhenSelfDriverReceivesSlipperyRoadEvents extends
    SelfDriverTestBase {

    @Test
    public void slipperyRoadEventsCauseSpeedChanges() {
        SensorEvent[] events = {
            speedLimit(50),
            simpleEvent(SLIPPERY_ROAD),
            simpleEvent(SLIPPERY_ROAD_CLEAR)
        };
        verifyEventResponses(NORMAL, events, expectSpeeds(50, 35, 50));
        verifyEventResponses(SPORT, events, expectSpeeds(55, 40, 55));
        verifyEventResponses(SAFE, events, expectSpeeds(45, 30, 45));
    }

    @Test
    public void slipperyRoadEventsAreIgnoredWhenOnSlipperyRoad() {
        SensorEvent[] events = {
            speedLimit(50),
            simpleEvent(SLIPPERY_ROAD),
            simpleEvent(SLIPPERY_ROAD)
        };

        verifyEventResponses(NORMAL, events, expectSpeeds(50, 35, 35));
        verifyEventResponses(SPORT, events, expectSpeeds(55, 40, 40));
        verifyEventResponses(SAFE, events, expectSpeeds(45, 30, 30));
    }

    @Test
    public void slipperyRoadClearEventsAreIgnoredWhenNotOnSlipperyRoad() {
        SensorEvent[] events = {
            speedLimit(50),
            simpleEvent(SLIPPERY_ROAD_CLEAR)
        };

        verifyEventResponses(NORMAL, events, expectSpeeds(50, 50));
        verifyEventResponses(SPORT, events, expectSpeeds(55, 55));
        verifyEventResponses(SAFE, events, expectSpeeds(45, 45));
    }

}
