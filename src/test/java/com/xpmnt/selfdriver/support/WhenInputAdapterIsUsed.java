package com.xpmnt.selfdriver.support;

import com.xpmnt.selfdriver.domain.DriveMode;
import com.xpmnt.selfdriver.domain.SelfDriver;
import com.xpmnt.selfdriver.domain.SensorEvent;
import com.xpmnt.selfdriver.domain.SensorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.xpmnt.selfdriver.support.Result.success;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class WhenInputAdapterIsUsed {

    private SelfDriver sd;
    private SensorEvent event;

    @BeforeEach
    public void setup() {
        initMocks(this);
        sd = mock(SelfDriver.class);
        event = mock(SensorEvent.class);
    }

    @Test
    public void itCreatesASelfDriverWithSpecifiedMode() {
        verifyStart(DriveMode.NORMAL);
        verifyStart(DriveMode.SPORT);
        verifyStart(DriveMode.SAFE);
    }

    @Test
    public void adapterReturnsStartSpeedWhenStarted() {
        initMocks(this);
        SelfDriver sd = mock(SelfDriver.class);
        final int expectedSpeed = 37;
        when(sd.speed()).thenReturn(expectedSpeed);
        DefaultInputAdapter adapter = DefaultInputAdapter.create("NORMAL", sd)
            .getOutput();

        int startSpeed = adapter.speed();

        assertEquals(expectedSpeed, startSpeed);
    }

    @Test
    public void adapterReturnsSpeedForValidEvent() {
        final Integer expectedSpeed = 43;
        when(sd.handleSensorEvent(any(SensorEvent.class)))
            .thenReturn(Result.success());
        when(sd.speed()).thenReturn(expectedSpeed);
        DefaultInputAdapter adapter = DefaultInputAdapter.create("NORMAL", sd)
            .getOutput();

        Result<String> result = adapter.handleInput("7");

        assertEquals(expectedSpeed.toString(),result.getOutput());
    }

    @Test
    public void factoryMethodFailsOnInvalidDriveModeName() {
        Result<DefaultInputAdapter> result = DefaultInputAdapter.create("BadModeName");
        assertTrue(result.failed());
        assertEquals("BadModeName is not a valid drive mode name.",
            result.getErrorMessage());
    }

    @Test
    public void usageExcludesInvalidEventTypes() {
        Result<DefaultInputAdapter> result = DefaultInputAdapter.create
            ("NORMAL");
        InputAdapter adapter = result.getOutput();

        String[] usage = adapter.usage();

        for (String line: usage){
            if (line.contains(SensorType.NOT_A_VALID_TYPE.name)){
                fail("Invalid types not excluded.");
            }
        }
    }


    private DefaultInputAdapter verifyStart(DriveMode mode) {
        Result<DefaultInputAdapter> result =
            DefaultInputAdapter.create(mode.name());
        if(result.failed()){
            fail(result.getErrorMessage());
        }
        DefaultInputAdapter adapter = result.getOutput();
        verifyStart(adapter, mode);
        return adapter;
    }

    private void verifyStart(DefaultInputAdapter adapter, DriveMode expectedMode) {
        Result<String> result = adapter.run();

        assertEquals("Welcome. Driving mode is " + expectedMode.name(),
            result.getOutput());
    }

}
