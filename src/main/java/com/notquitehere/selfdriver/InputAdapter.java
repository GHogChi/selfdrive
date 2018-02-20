package com.notquitehere.selfdriver;

import com.notquitehere.selfdriver.util.Result;

import static com.notquitehere.selfdriver.util.Result.failure;
import static com.notquitehere.selfdriver.util.Result.success;

public class InputAdapter {
    private final SelfDriver selfDriver;

    public InputAdapter(DriveMode mode) {
        selfDriver = new SelfDriver(mode);
    }

    InputAdapter(SelfDriver injectedDriver) {
        selfDriver = injectedDriver;
    }

    public Result<String> start() {
        return success("Welcome. Driving mode is " + selfDriver.mode().name());
    }

    public int speed() {
        return selfDriver.speed();
    }

    public Result<Integer> handle(SensorEvent event) {
        Result<Void> result = selfDriver.handleSensorEvent(event);
        if (result.failed()){
            return failure(result.getErrorMessage());
        }
        return success(selfDriver.speed());
    }
}
