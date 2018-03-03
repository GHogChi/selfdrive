package com.xpmnt.selfdriver.domain;

import org.junit.jupiter.api.Test;

public class WhenSelfDriverReceivesWeatherEvents extends SelfDriverTestBase {

    @Test
    public void weatherEventsCauseSpeedChanges() {
        SensorEvent[] events = {
            speedLimit(50),
            SensorEvent.simpleEvent(SensorType.WEATHER_RAINY),
            SensorEvent.simpleEvent(SensorType.WEATHER_CLEAR)
        };

        verifyEventResponses(DriveMode.NORMAL, events, expectSpeeds(50, 45, 50));
        verifyEventResponses(DriveMode.SPORT, events, expectSpeeds(55, 50, 55));
        verifyEventResponses(DriveMode.SAFE, events, expectSpeeds(45, 40, 45));
    }

    @Test
    public void weatherRainyCannotBeAppliedIfRaining() {
        SensorEvent[] events = {
            speedLimit(50),
            SensorEvent.simpleEvent(SensorType.WEATHER_RAINY),
            SensorEvent.simpleEvent(SensorType.WEATHER_RAINY)
        };
        verifyEventResponses(DriveMode.NORMAL, events, expectSpeeds(50, 45, 45));
        verifyEventResponses(DriveMode.SPORT, events, expectSpeeds(55, 50, 50));
        verifyEventResponses(DriveMode.SAFE, events, expectSpeeds(45, 40, 40));

    }

    @Test
    public void weatherClearCannotBeAppliedIfNotRaining() {
        SensorEvent[] events = {
            speedLimit(50),
            SensorEvent.simpleEvent(SensorType.WEATHER_CLEAR)
        };
        verifyEventResponses(DriveMode.NORMAL, events, expectSpeeds(50, 50));
        verifyEventResponses(DriveMode.SPORT, events, expectSpeeds(55, 55));
        verifyEventResponses(DriveMode.SAFE, events, expectSpeeds(45, 45));

    }
}
