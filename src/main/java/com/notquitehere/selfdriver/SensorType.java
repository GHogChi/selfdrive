package com.notquitehere.selfdriver;

import java.util.HashMap;
import java.util.Map;

public class SensorType {
    public static final SensorType TRAFFIC = new SensorType("TRAFFIC", 1);
    public static final SensorType TRAFFIC_CLEAR = new SensorType("TRAFFIC_CLEAR", 2);
    public static final SensorType WEATHER_RAINY = new SensorType("WEATHER_RAINY", 3);
    public static final SensorType WEATHER_CLEAR = new SensorType("WEATHER_CLEAR", 4);
    public static final SensorType SLIPPERY_ROAD = new SensorType("SLIPPERY_ROAD", 5);
    public static final SensorType SLIPPERY_ROAD_CLEAR = new SensorType("SLIPPERY_ROAD_CLEAR", 6);
    public static final SensorType EMERGENCY_TURBO = new SensorType("EMERGENCY_TURBO", 7);
    public static final SensorType SPEED_LIMIT_SIGN =
            new SensorType("SPEED_LIMIT_SIGN", SpeedLimit.class, 10);

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
    }

    private void register(int id, SensorType event) {
        if (eventsById == null){
            eventsById = new HashMap<>();
        }
        if(eventsById.containsKey(id)){
            throw new IllegalArgumentException("Id already in use: "+ id);
        }
        eventsById.put(id, event);
    }

}



