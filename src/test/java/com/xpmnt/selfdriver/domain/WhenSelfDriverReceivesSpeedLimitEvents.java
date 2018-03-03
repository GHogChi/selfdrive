package com.xpmnt.selfdriver.domain;

import org.junit.jupiter.api.Test;


public class WhenSelfDriverReceivesSpeedLimitEvents extends SelfDriverTestBase{

    @Test
    public void speedLimitSignEventCausesSpeedChange() {
        final SensorEvent[] events =
            {SensorEvent.speedLimitSignEvent(47)};

        verifyFinalSpeed(DriveMode.NORMAL, events, 47);
        verifyFinalSpeed(DriveMode.SPORT, events, 52);
        verifyFinalSpeed(DriveMode.SAFE, events, 42);
    }
}
