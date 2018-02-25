package com.notquitehere.selfdriver.domain;

import com.notquitehere.selfdriver.support.Result;
import org.junit.jupiter.api.Assertions;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.notquitehere.selfdriver.domain.SensorEvent
    .speedLimitSignEvent;

/**
 * Provides support and definitions for tests of the SelfDriver.
 * To add a new SensorEvent, extend this class.
 */
public class SelfDriverTestBase {

    protected void verifyExpectations(DriveMode mode,
                                      Expectation[] expectations) {
        SelfDriver sd = new SelfDriver(mode);
        for (Expectation expectation : expectations) {
            Result<Void> result = sd.handleSensorEvent(expectation.event);
            if (result.failed()) {
                Assertions.fail(result.getErrorMessage());
            }
            if (sd.speed() != expectation.expectedSpeed) {
                Assertions.fail("Expected speed to be " + expectation.expectedSpeed +
                    " after " + expectation.event.type.name +
                    " but it was " + sd.speed());
            }
        }
    }

    protected void verifyEventResponses(DriveMode mode, SensorEvent[] events,
                                        int[] expectedSpeeds) {
        if (events.length != expectedSpeeds.length) {
            Assertions.fail("Really? Different lengths?");
        }
        Expectation[] expectations = IntStream
            .range(0, events.length)
            .mapToObj(i -> new Expectation(events[i], expectedSpeeds[i]))
            .collect(Collectors.toList())
            .toArray(new Expectation[0]);
        verifyExpectations(mode, expectations);
    }

    protected void verifyFinalSpeed(
        DriveMode mode, SensorEvent[] events, int expectedSpeed) {
        SelfDriver driver = new SelfDriver(mode);
        for (SensorEvent event : events) {
            Result<Void> result = driver.handleSensorEvent(event);
            if (result.failed()) {
                Assertions.fail(result.getErrorMessage());
            }
        }
        Assertions.assertEquals(expectedSpeed, driver.speed());
    }

    protected SensorEvent speedLimit(int limit) {
        return speedLimitSignEvent(limit);
    }

    protected int[] expectSpeeds(int... expectedSpeeds){
        return expectedSpeeds;
    }
}
