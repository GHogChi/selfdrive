package com.notquitehere.selfdriver;

import com.notquitehere.selfdriver.util.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.notquitehere.selfdriver.DriveMode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        verifyStart(NORMAL);
        verifyStart(SPORT);
        verifyStart(SAFE);
    }

    @Test
    public void adapterReturnsStartSpeedWhenStarted() {
        initMocks(this);
        SelfDriver sd = mock(SelfDriver.class);
        final int expectedSpeed = 37;
        when(sd.speed()).thenReturn(expectedSpeed);
        InputAdapter adapter = new InputAdapter(sd);

        int startSpeed = adapter.speed();

        assertEquals(expectedSpeed, startSpeed);
    }

    @Test
    public void adapterReturnsSpeedForValidEvent() {
        final int expectedSpeed = 43;
        when(sd.handleSensorEvent(event)).thenReturn(Result.success());
        when(sd.speed()).thenReturn(expectedSpeed);
        InputAdapter adapter = new InputAdapter(sd);

        Result<Integer> result = adapter.handle(event);

        assertTrue(expectedSpeed == result.getOutput());
    }


    private InputAdapter verifyStart(DriveMode mode) {
        InputAdapter adapter = new InputAdapter(mode);
        verifyStart(adapter, mode);
        return adapter;
    }

    private void verifyStart(InputAdapter adapter, DriveMode expectedMode) {
        Result<String> result = adapter.start();

        assertEquals("Welcome. Driving mode is " + expectedMode.name(),
            result.getOutput());
    }

}
