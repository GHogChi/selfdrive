package com.notquitehere.selfdriver;

import com.notquitehere.selfdriver.util.Result;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.notquitehere.selfdriver.DriveMode.*;
import static com.notquitehere.selfdriver.ExpectInvalid.expectInvalid;
import static com.notquitehere.selfdriver.Expectation.expect;
import static com.notquitehere.selfdriver.SensorEvent.create;
import static com.notquitehere.selfdriver.SensorEvent.createSimple;
import static com.notquitehere.selfdriver.SensorType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class WhenSelfDriverIsStarted {

    @Test
    public void startingSpeedIsCorrect() {
        SelfDriver driver = new SelfDriver(NORMAL);

        assertEquals(20, driver.speed());
    }

    @Test
    public void trafficEventsCauseSpeedChanges() {
        SensorEvent[] events = {
            create(SPEED_LIMIT_SIGN, limit(50)),
            createSimple(TRAFFIC),
            createSimple(TRAFFIC_CLEAR)
        };

        verifyExpectations(NORMAL, events, new int[]{50, 40, 50});
        verifyExpectations(SPORT, events, new int[]{55, 50, 55});
        verifyExpectations(SAFE, events, new int[]{45, 30, 45});
    }

    @Test
    public void weatherEventsCauseSpeedChanges() {
        SensorEvent[] events = {
            create(SPEED_LIMIT_SIGN, limit(50)),
            createSimple(WEATHER_RAINY),
            createSimple(WEATHER_CLEAR)
        };
        verifyExpectations(NORMAL, events, new int[]{50, 45, 50});
        verifyExpectations(SPORT, events, new int[]{55, 50, 55});
        verifyExpectations(SAFE, events, new int[]{45, 40, 45});
    }

    @Test
    public void speedLimitSignCausesSpeedSetting() {
        final SensorEvent[] events =
            {create(SPEED_LIMIT_SIGN, limit(47))};

        verifyFinalSpeed(NORMAL, events, 47);
        verifyFinalSpeed(SPORT, events, 52);
        verifyFinalSpeed(SAFE, events, 42);
    }

    @Test
    public void executesSuccessScenario() {
        final SensorEvent[] events = {
            create(SPEED_LIMIT_SIGN, limit(50)),
            createSimple(TRAFFIC),
            createSimple(TRAFFIC_CLEAR)
        };
        verifyExpectations(NORMAL, events, new int[]{50, 40, 50});
        Expectation[] expectations = new Expectation[]{
            expect(create(SPEED_LIMIT_SIGN, limit(50)), 50),
            expect(createSimple(TRAFFIC), 40),
            expect(createSimple(TRAFFIC_CLEAR), 50)
        };
        verifyExpectations(NORMAL, expectations);
    }

    private SpeedLimit limit(int limit) {
        return new SpeedLimit(limit);
    }

    @Test
    public void executesIgnoreScenario() {
        Expectation[] expectations = new Expectation[]{
            expect(createSimple(EMERGENCY_TURBO), 50),
            expect(createSimple(EMERGENCY_TURBO), 50)
        };
        verifyExpectations(SPORT, expectations);
    }

    /**
     * This test is ignored because the upper bound on speed limits
     * is enforced by the SpeedLimit class: the limit of 200 can't be specified.
     */
    @Test
    @Disabled
    public void executesSafeScenario() {
        Expectation[] expectations = new Expectation[]{
            expect(create(SPEED_LIMIT_SIGN, limit(60)), 55),
            expectInvalid(create(SPEED_LIMIT_SIGN, limit(200)))
        };
        verifyExpectations(SAFE, expectations);
    }

    private void verifyExpectations(DriveMode mode,
                                    Expectation[] expectations) {
        SelfDriver sd = new SelfDriver(mode);
        for (Expectation expectation : expectations) {
            Result<Void> result = sd.handleSensorEvent(expectation.event);
            if (result.failed()) {
                fail(result.getErrorMessage());
            }
            if (sd.speed() != expectation.expectedSpeed) {
                fail("Expected speed to be " + expectation.expectedSpeed +
                    " after " + expectation.event.type.name +
                    " but it was " + sd.speed());
            }
        }
    }

    private void verifyExpectations(DriveMode mode, SensorEvent[] events,
                                    int[] expectedSpeeds) {
        if (events.length != expectedSpeeds.length) {
            fail("Really? Different lengths?");
        }
        Expectation[] expectations = IntStream
            .range(0, events.length)
            .mapToObj(i -> new Expectation(events[i], expectedSpeeds[i]))
            .collect(Collectors.toList())
            .toArray(new Expectation[0]);
        verifyExpectations(mode, expectations);
    }

    private void verifyFinalSpeed(
        DriveMode mode, SensorEvent[] events, int expectedSpeed) {
        SelfDriver driver = new SelfDriver(mode);
        for (SensorEvent event : events) {
            Result<Void> result = driver.handleSensorEvent(event);
            if (result.failed()) {
                fail(result.getErrorMessage());
            }
        }
        assertEquals(expectedSpeed, driver.speed());
    }

}

class Expectation {

    public final SensorEvent event;
    public final Integer expectedSpeed;

    protected Expectation(SensorEvent event, Integer expectedSpeed) {
        this.event = event;
        this.expectedSpeed = expectedSpeed;
    }

    public static Expectation expect(SensorEvent e, Integer s) {
        return new Expectation(e, s);
    }

}

class ExpectInvalid extends Expectation {

    private ExpectInvalid(SensorEvent event) {
        super(event, 0);
    }

    public static ExpectInvalid expectInvalid(SensorEvent e) {
        return new ExpectInvalid(e);
    }
}
