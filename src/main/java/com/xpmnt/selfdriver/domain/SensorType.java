package com.xpmnt.selfdriver.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * SensorType is a flyweight class similar to an enum but with structural
 * complexities making it impossible to represent as a Java Enum. (Perhaps a
 * refactoring is in order to separate out the anomalous speed limit type.
 * As a flyweight with private constructors, it allows reference equality
 * checks.
 * <p>
 * todo separate out speed limit sign to simplify this class
 */
public class SensorType {
    public static final SensorType PARK_AND_SHUT_DOWN = new SensorType
        ("PARK_AND_SHUT_DOWN ", 0);
    public static final SensorType TRAFFIC = new SensorType("TRAFFIC", 1);
    public static final SensorType TRAFFIC_CLEAR =
        new SensorType("TRAFFIC_CLEAR", 2);
    public static final SensorType WEATHER_RAINY =
        new SensorType("WEATHER_RAINY", 3);
    public static final SensorType WEATHER_CLEAR =
        new SensorType("WEATHER_CLEAR", 4);
    public static final SensorType SLIPPERY_ROAD =
        new SensorType("SLIPPERY_ROAD", 5);
    public static final SensorType SLIPPERY_ROAD_CLEAR =
        new SensorType("SLIPPERY_ROAD_CLEAR", 6);
    public static final SensorType EMERGENCY_TURBO =
        new SensorType("EMERGENCY_TURBO", 7);
    public static final SensorType SPEED_LIMIT_SIGN =
        new SensorType("SPEED_LIMIT_SIGN", SpeedLimit.class, 10);
    public static final SensorType NOT_A_VALID_TYPE =
        new SensorType("NOT_A_VALID_TYPE", Integer.MAX_VALUE);

    private static int MIN_ID = Integer.MAX_VALUE;
    private static int MAX_ID = Integer.MIN_VALUE;

    private static Map<Integer, SensorType> eventsById;
    public final String name;
    public final int id;
    public final Class<?> parameterClass;

    private SensorType(String name, int id) {
        this(name, null, id);
    }

    private SensorType(String name, Class<?> parameterClass, int id) {
        register(id, this);
        this.name = name;
        this.id = id;
        this.parameterClass = parameterClass;
        if (name.equals("NOT_A_VALID_TYPE")) {return;}
        if (id < MIN_ID) MIN_ID = id;
        if (id > MAX_ID) MAX_ID = id;
    }

    private void register(int id, SensorType event) {
        if (eventsById == null) {
            eventsById = new HashMap<>();
        }
        if (eventsById.containsKey(id)) {
            throw new IllegalArgumentException("Id already in use: " + id);
        }
        eventsById.put(id, event);
    }

    public static SensorType typeForCode(int code) {
        return (eventsById.containsKey(code))
            ? eventsById.get(code)
            : SpeedLimit.speedIsValid(code)
            ? SPEED_LIMIT_SIGN : NOT_A_VALID_TYPE
            ;
    }

    public static int getMinId() {
        return MIN_ID;
    }

    public static int getMaxId() {
        return MAX_ID;
    }
}



