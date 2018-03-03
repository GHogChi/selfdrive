package com.xpmnt.selfdriver.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class WhenSelfDriverIsStarted extends SelfDriverTestBase {

    @Test
    public void startingSpeedIsCorrect() {
        SelfDriver driver = new SelfDriver(DriveMode.NORMAL);

        Assertions.assertEquals(20, driver.speed());
    }

    @Test
    public void itExecutesSuccessScenario() {
        final SensorEvent[] events = {
            SensorEvent.speedLimitSignEvent(50),
            SensorEvent.simpleEvent(SensorType.TRAFFIC),
            SensorEvent.simpleEvent(SensorType.TRAFFIC_CLEAR)
        };
        verifyEventResponses(DriveMode.NORMAL, events, new int[]{50, 40, 50});
        Expectation[] expectations = new Expectation[]{
            Expectation.expect(speedLimit(50), 50),
            Expectation.expect(SensorEvent.simpleEvent(SensorType.TRAFFIC), 40),
            Expectation.expect(SensorEvent.simpleEvent(SensorType.TRAFFIC_CLEAR), 50)
        };
        verifyExpectations(DriveMode.NORMAL, expectations);
    }

    @Test
    public void itExecutesIgnoreScenario() {
        Expectation[] expectations = new Expectation[]{
            Expectation.expect(
                SensorEvent.simpleEvent(SensorType.EMERGENCY_TURBO), 50),
            Expectation.expect(
                SensorEvent.simpleEvent(SensorType.EMERGENCY_TURBO), 50)
        };
        verifyExpectations(DriveMode.SPORT, expectations);
    }

    /**
     * This test is disabled because the upper bound on speed limits
     * is enforced by the SpeedLimit class: the limit of 200 can't be specified.
     */
    @Test
    @Disabled
    public void itExecutesSafeScenario() {
        Expectation[] expectations = new Expectation[]{
            Expectation.expect(speedLimit(60), 55),
            ExpectInvalid.expectInvalid(speedLimit(200))
        };
        verifyExpectations(DriveMode.SAFE, expectations);
    }

}

