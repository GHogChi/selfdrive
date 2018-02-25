package com.notquitehere.selfdriver.support;

import com.notquitehere.selfdriver.domain.SelfDriver;
import com.notquitehere.selfdriver.domain.SensorEvent;
import com.notquitehere.selfdriver.domain.DriveMode;
import com.notquitehere.selfdriver.domain.SensorType;

import java.util.Optional;
import java.util.function.Function;

import static com.notquitehere.selfdriver.domain.SensorEvent.simpleEvent;
import static com.notquitehere.selfdriver.domain.SensorType.*;
import static com.notquitehere.selfdriver.support.Result.failure;
import static com.notquitehere.selfdriver.support.Result.success;
import static java.text.MessageFormat.format;

public class DefaultInputAdapter implements InputAdapter {
    private final SelfDriver selfDriver;
    private boolean acceptingInput;


    /**
     * Example of OOFP Exception elimination rule.
     *
     * @param modeName
     * @return success iff modeName is valid, else failure
     */
    public static Result<DefaultInputAdapter> create(String modeName) {
        return getInputAdapterResult(
            modeName.toUpperCase(), Optional.ofNullable(null));
    }

    static Result<DefaultInputAdapter> create(String modeName, SelfDriver driver) {
        return getInputAdapterResult(modeName, Optional.of(driver));
    }

    private DefaultInputAdapter(String modeName, Optional<SelfDriver> driverOption)
        throws IllegalArgumentException {

        DriveMode driveMode;
        try {
            driveMode = Enum.valueOf(DriveMode.class, modeName);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                format("{0} is not a valid drive mode name.", modeName)
            );
        }
        selfDriver = (driverOption.isPresent())
            ? driverOption.get()
            : new SelfDriver(driveMode);
        acceptingInput = true;
    }

    DefaultInputAdapter(SelfDriver injectedDriver) {
        selfDriver = injectedDriver;
    }

    @Override
    public Result<String> run() {
        return success("Welcome. Driving mode is " + selfDriver.mode().name());
    }

    @Override
    public Function<String, Result<String>> getInputHandler() {
        return this::handleInput;
    }

    @Override
    public int speed() {
        return selfDriver.speed();
    }

    @Override
    public boolean isAcceptingInput() {
        return acceptingInput;
    }

    @Override
    public Result<String> handleInput(String eventId) {
        int id = Integer.parseInt(eventId);
        SensorType sensorType = typeForCode(id);
        if (sensorType == NOT_A_VALID_TYPE) {
            return failure("Not a valid event type");
        }
        SensorEvent event =
            (sensorType == SPEED_LIMIT_SIGN)
                ? SensorEvent.speedLimitSignEvent(id)
                : simpleEvent(sensorType);
        if (event.type == PARK_AND_SHUT_DOWN) {
            acceptingInput = false;
            return success("Parking and shutting down....");
        }
        final Result<Integer> handlerResult = handle(event);
        return (handlerResult.succeeded())
            ? success(handlerResult.getOutput().toString())
            : failure(handlerResult.getErrorMessage());
    }

    @Override
    public String modeName() {
        return selfDriver.mode().name();
    }

    private Result<Integer> handle(SensorEvent event) {
        Result<Void> result = selfDriver.handleSensorEvent(event);
        if (result.failed()) {
            return failure(result.getErrorMessage());
        }
        return success(selfDriver.speed());
    }

    private static Result<DefaultInputAdapter> getInputAdapterResult(
        String modeName, Optional<SelfDriver> driverOption) {
        try {
            DefaultInputAdapter adapter = new DefaultInputAdapter(modeName,
                driverOption);
            return success(adapter);
        } catch (Throwable e) {
            return failure(e.getLocalizedMessage());
        }
    }
}
