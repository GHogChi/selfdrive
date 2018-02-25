package com.notquitehere.selfdriver.domain;

import org.junit.jupiter.api.Test;

import static com.notquitehere.selfdriver.domain.DriveMode.NORMAL;
import static com.notquitehere.selfdriver.domain.DriveMode.SAFE;
import static com.notquitehere.selfdriver.domain.DriveMode.SPORT;
import static com.notquitehere.selfdriver.domain.SensorEvent.speedLimitSignEvent;


public class WhenSelfDriverReceivesSpeedLimitEvents extends SelfDriverTestBase{

    @Test
    public void speedLimitSignEventCausesSpeedChange() {
        final SensorEvent[] events =
            {speedLimitSignEvent(47)};

        verifyFinalSpeed(NORMAL, events, 47);
        verifyFinalSpeed(SPORT, events, 52);
        verifyFinalSpeed(SAFE, events, 42);
    }
}
