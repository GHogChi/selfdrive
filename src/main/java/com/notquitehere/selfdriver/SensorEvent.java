package com.notquitehere.selfdriver;

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

    public static <T> SensorEvent create(
            SensorType type, T parameter) {
        return new SensorEvent(type, parameter);
    }

    public static SensorEvent createSimple(SensorType type) {
        return new SensorEvent(type);
    }
}
