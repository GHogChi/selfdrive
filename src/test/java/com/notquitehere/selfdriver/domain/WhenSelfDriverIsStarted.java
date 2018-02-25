package com.notquitehere.selfdriver.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static com.notquitehere.selfdriver.domain.DriveMode.*;
import static com.notquitehere.selfdriver.domain.ExpectInvalid.expectInvalid;
import static com.notquitehere.selfdriver.domain.Expectation.expect;
import static com.notquitehere.selfdriver.domain.SensorEvent.simpleEvent;
import static com.notquitehere.selfdriver.domain.SensorEvent
    .speedLimitSignEvent;
import static com.notquitehere.selfdriver.domain.SensorType.*;

public class WhenSelfDriverIsStarted extends SelfDriverTestBase {

    @Test
    public void startingSpeedIsCorrect() {
        SelfDriver driver = new SelfDriver(NORMAL);

        Assertions.assertEquals(20, driver.speed());
    }

    @Test
    public void itExecutesSuccessScenario() {
        final SensorEvent[] events = {
            speedLimitSignEvent(50),
            simpleEvent(TRAFFIC),
            simpleEvent(TRAFFIC_CLEAR)
        };
        verifyEventResponses(NORMAL, events, new int[]{50, 40, 50});
        Expectation[] expectations = new Expectation[]{
            expect(speedLimit(50), 50),
            expect(simpleEvent(TRAFFIC), 40),
            expect(simpleEvent(TRAFFIC_CLEAR), 50)
        };
        verifyExpectations(NORMAL, expectations);
    }

    @Test
    public void itExecutesIgnoreScenario() {
        Expectation[] expectations = new Expectation[]{
            expect(simpleEvent(EMERGENCY_TURBO), 50),
            expect(simpleEvent(EMERGENCY_TURBO), 50)
        };
        verifyExpectations(SPORT, expectations);
    }

    /**
     * This test is disabled because the upper bound on speed limits
     * is enforced by the SpeedLimit class: the limit of 200 can't be specified.
     */
    @Test
    @Disabled
    public void itExecutesSafeScenario() {
        Expectation[] expectations = new Expectation[]{
            expect(speedLimit(60), 55),
            expectInvalid(speedLimit(200))
        };
        verifyExpectations(SAFE, expectations);
    }

}

