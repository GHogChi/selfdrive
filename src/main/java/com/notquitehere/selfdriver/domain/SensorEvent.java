package com.notquitehere.selfdriver.domain;

import static com.notquitehere.selfdriver.domain.SensorType.NOT_A_VALID_TYPE;
import static com.notquitehere.selfdriver.domain.SensorType.SPEED_LIMIT_SIGN;
import static com.notquitehere.selfdriver.domain.SpeedLimit.limit;

//todo Object is a code smell - refactor
public class SensorEvent {
    public final SensorType type;
    //todo use of Object is a code smell - fix it
    public final Object parameter;

    public SensorEvent(SensorType type, Object parameter) {
        this.type = type;
        this.parameter = parameter;
        if (type.parameterClass != null){
            if (parameter == null || !(type.parameterClass.isAssignableFrom
                    (parameter.getClass()))){
                throw new IllegalArgumentException(
                        "Invalid or null parameter for sensor type"
                );
            }
        }
    }

    public SensorEvent(SensorType type) {
        this(type, null);
    }

    public static SensorEvent speedLimitSignEvent(int limitValue) {
        return new SensorEvent(SPEED_LIMIT_SIGN, limit(limitValue));
    }

    public static SensorEvent simpleEvent(SensorType type) {
        return new SensorEvent(type);
    }

    public static final SensorEvent NOT_A_VALID_EVENT =
        new SensorEvent(NOT_A_VALID_TYPE);
}
