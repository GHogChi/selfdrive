package com.notquitehere.selfdriver;

import com.notquitehere.selfdriver.util.Result;
import org.junit.jupiter.api.Test;

import static com.notquitehere.selfdriver.DriveMode.*;
import static com.notquitehere.selfdriver.SensorType.SPEED_LIMIT_SIGN;
import static com.notquitehere.selfdriver.SensorType.TRAFFIC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class WhenSelfDriverIsStarted {

    @Test
    public void startingSpeedIsCorrect() {
        SelfDriver driver = new SelfDriver(NORMAL);

        assertEquals(20, driver.speed());
    }

    @Test
    public void trafficEventCausesSpeedChange(){
        SensorEvent[] events = {SensorEvent.createSimple(TRAFFIC)};

        verifyFinalSpeed(NORMAL, events, 10);
        verifyFinalSpeed(SPORT, events, 15);
        verifyFinalSpeed(SAFE, events, 10);

    }

    @Test
    public void speedLimitSignCausesSpeedSetting(){
        final SensorEvent[] events =
            {SensorEvent.create(SPEED_LIMIT_SIGN, new SpeedLimit(47))};
        verifyFinalSpeed(NORMAL, events, 47);
        verifyFinalSpeed(SPORT, events, 52);
        verifyFinalSpeed(SAFE, events, 42);

    }


    private void verifyFinalSpeed(
        DriveMode mode, SensorEvent[] events, int expectedSpeed) {
        SelfDriver driver = new SelfDriver(mode);
        for(SensorEvent event: events){
            Result<Void> result = driver.handleSensorEvent(event);
            if(result.failed()){
                fail(result.getErrorMessage());
            }
        }
        assertEquals(expectedSpeed, driver.speed());
    }
}
