package com.notquitehere.selfdriver.domain;

import org.junit.jupiter.api.Test;

import static com.notquitehere.selfdriver.domain.DriveMode.NORMAL;
import static com.notquitehere.selfdriver.domain.DriveMode.SAFE;
import static com.notquitehere.selfdriver.domain.DriveMode.SPORT;
import static com.notquitehere.selfdriver.domain.SensorEvent.simpleEvent;
import static com.notquitehere.selfdriver.domain.SensorType.*;

public class WhenSelfDriverReceivesWeatherEvents extends SelfDriverTestBase {

    @Test
    public void weatherEventsCauseSpeedChanges() {
        SensorEvent[] events = {
            speedLimit(50),
            simpleEvent(WEATHER_RAINY),
            simpleEvent(WEATHER_CLEAR)
        };

        verifyEventResponses(NORMAL, events, expectSpeeds(50, 45, 50));
        verifyEventResponses(SPORT, events, expectSpeeds(55, 50, 55));
        verifyEventResponses(SAFE, events, expectSpeeds(45, 40, 45));
    }

    @Test
    public void weatherRainyCannotBeAppliedIfRaining() {
        SensorEvent[] events = {
            speedLimit(50),
            simpleEvent(WEATHER_RAINY),
            simpleEvent(WEATHER_RAINY)
        };
        verifyEventResponses(NORMAL, events, expectSpeeds(50, 45, 45));
        verifyEventResponses(SPORT, events, expectSpeeds(55, 50, 50));
        verifyEventResponses(SAFE, events, expectSpeeds(45, 40, 40));

    }

    @Test
    public void weatherClearCannotBeAppliedIfNotRaining() {
        SensorEvent[] events = {
            speedLimit(50),
            simpleEvent(WEATHER_CLEAR)
        };
        verifyEventResponses(NORMAL, events, expectSpeeds(50, 50));
        verifyEventResponses(SPORT, events, expectSpeeds(55, 55));
        verifyEventResponses(SAFE, events, expectSpeeds(45, 45));

    }
}
