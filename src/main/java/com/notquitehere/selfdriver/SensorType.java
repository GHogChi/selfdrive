package com.notquitehere.selfdriver;

import java.util.HashMap;
import java.util.Map;

public class SensorType {
    public static final SensorType TRAFFIC = new SensorType(1);
    public static final SensorType TRAFFIC_CLEAR = new SensorType(2);
    public static final SensorType WEATHER_RAINY = new SensorType(3);
    public static final SensorType WEATHER_CLEAR = new SensorType(4);
    public static final SensorType SLIPPERY_ROAD = new SensorType(5);
    public static final SensorType SLIPPERY_ROAD_CLEAR = new SensorType(6);
    public static final SensorType EMERGENCY_TURBO = new SensorType(7);
    public static final SensorType SPEED_LIMIT_SIGN =
            new SensorType(10, SpeedLimit.class);

    private static Map<Integer, SensorType> eventsById;
    public final int id;
    public final Class<?> parameterClass;

    private SensorType(int id) {
        this(id, null);
    }

    private SensorType(int id, Class<?> parameterClass) {
        register(id, this);
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



