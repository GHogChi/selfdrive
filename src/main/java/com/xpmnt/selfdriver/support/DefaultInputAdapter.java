package com.xpmnt.selfdriver.support;

import com.xpmnt.selfdriver.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static com.xpmnt.selfdriver.domain.SensorEvent.simpleEvent;
import static com.xpmnt.selfdriver.domain.SensorType.*;
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
        return create(modeName, Optional.empty());
    }

    static Result<DefaultInputAdapter> create(String modeName, SelfDriver driver) {
        return create(modeName, Optional.of(driver));
    }

    private DefaultInputAdapter(String modeName, Optional<SelfDriver> driverOption)
        throws IllegalArgumentException {

        DriveMode driveMode;
        try {
            driveMode = Enum.valueOf(DriveMode.class, modeName.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException(
                format("{0} is not a valid drive mode name.", modeName)
            );
        }
        selfDriver = driverOption.orElseGet(() -> new SelfDriver(driveMode));
        acceptingInput = true;
    }

    DefaultInputAdapter(SelfDriver injectedDriver) {
        selfDriver = injectedDriver;
    }

    @Override
    public Result<String> run() {
        return Result.success("Welcome. Driving mode is " + selfDriver.mode().name());
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
            return Result.failure("INVALID");
        }
        SensorEvent event =
            (sensorType == SPEED_LIMIT_SIGN)
                ? SensorEvent.speedLimitSignEvent(id)
                : simpleEvent(sensorType);
        if (event.type == PARK_AND_SHUT_DOWN) {
            acceptingInput = false;
            return Result.success("Parking and shutting down....");
        }
        final Result<Integer> handlerResult = handle(event);
        return (handlerResult.succeeded())
            ? Result.success(handlerResult.getOutput().toString())
            : Result.failure(handlerResult.getErrorMessage());
    }

    @Override
    public String modeName() {
        return selfDriver.mode().name();
    }

    @Override
    public String[] usage() {
        List<String> lines = new ArrayList<>();
        for (int i = getMinId(); i <= getMaxId(); ++i){
            SensorType type = SensorType.typeForCode(i);
            if (type == NOT_A_VALID_TYPE) continue;
            if (type == SPEED_LIMIT_SIGN) {
                lines.add(String.format(
                    "%3d to %d: SPEED_LIMIT_SIGN",
                    SpeedLimit.LOWER_BOUND,
                    SpeedLimit.UPPER_BOUND
                ));
            } else {
                final String name = type.name;
                lines.add(String.format("%3d: %s", i, name));
            }
        }
        return lines.toArray(new String[0]);
    }

    private Result<Integer> handle(SensorEvent event) {
        Result<Void> result = selfDriver.handleSensorEvent(event);
        if (result.failed()) {
            return Result.failure(result.getErrorMessage());
        }
        return Result.success(selfDriver.speed());
    }

    private static Result<DefaultInputAdapter> create(
        String modeName, Optional<SelfDriver> driverOption) {
        try {
            DefaultInputAdapter adapter =
                new DefaultInputAdapter(modeName, driverOption);
            return Result.success(adapter);
        } catch (Throwable e) {
            return Result.failure(e.getLocalizedMessage());
        }
    }
}
