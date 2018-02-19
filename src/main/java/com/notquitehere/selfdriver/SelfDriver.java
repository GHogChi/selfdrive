package com.notquitehere.selfdriver;

import com.notquitehere.selfdriver.util.Result;
import com.notquitehere.selfdriver.util.UnmodifiableMapBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static com.notquitehere.selfdriver.DriveMode.NORMAL;
import static com.notquitehere.selfdriver.DriveMode.SAFE;
import static com.notquitehere.selfdriver.DriveMode.SPORT;
import static com.notquitehere.selfdriver.SensorType.*;
import static com.notquitehere.selfdriver.SpeedLimit.forceFit;
import static com.notquitehere.selfdriver.util.Result.failure;
import static com.notquitehere.selfdriver.util.Result.success;

public class SelfDriver {
    public final DriveMode mode;
    private int speed = 20;
    private boolean inTraffic = false;
    private boolean inRain = false;
    private boolean onSlipperyRoad = false;
    public static final int MIN_SPEED = 10;

    public boolean isInTraffic() {
        return inTraffic;
    }

    public void setInTraffic(boolean inTraffic) {
        this.inTraffic = inTraffic;
    }

    public boolean isInRain() {
        return inRain;
    }

    public void setInRain(boolean inRain) {
        this.inRain = inRain;
    }

    public boolean isOnSlipperyRoad() {
        return onSlipperyRoad;
    }

    public void setOnSlipperyRoad(boolean onSlipperyRoad) {
        this.onSlipperyRoad = onSlipperyRoad;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = forceFit(speed);
    }

    public void adjustSpeed(int delta){
        speed = forceFit(Math.max(speed + delta, MIN_SPEED));
    }

    public static final Map<DriveMode, Map<SensorType, Integer>>
        SPEEDMAPS_BY_DRIVE_MODE;
    private static final Map<SensorType, Action> actionsByType;

    static {
        SPEEDMAPS_BY_DRIVE_MODE = new HashMap<>();
        buildSpeedMaps();
        actionsByType = new HashMap<>();
        actionsByType.put(TRAFFIC, SelfDriver::handleTraffic);
        actionsByType.put(SPEED_LIMIT_SIGN, SelfDriver::handleSpeedLimit);

    }

    public SelfDriver(DriveMode mode) {
        this.mode = mode;
    }

    public Result<Void> handleSensorEvent(SensorEvent event) {
        Action action = actionsByType.get(event.type);
        return action.apply(this, event);
    }

    public int speed() {
        return speed;
    }

    private static Result<Void> handleTraffic(SelfDriver sd, SensorEvent e) {
        sd.setInTraffic(true);
        sd.adjustSpeed(getDelta(sd, TRAFFIC));
        return success();
    }

    private static Result<Void> handleSpeedLimit(SelfDriver sd, SensorEvent e) {
        int limit = ((SpeedLimit) e.parameter).limit;
        sd.setSpeed(limit + getDelta(sd, SPEED_LIMIT_SIGN));
        return success();
    }

    private static Integer getDelta(SelfDriver sd, SensorType sensorType) {
        return SPEEDMAPS_BY_DRIVE_MODE.get(sd.mode).get(sensorType);
    }

    //todo load from files, env vars, DB or ...
    private static void buildSpeedMaps() {
        SPEEDMAPS_BY_DRIVE_MODE.put(
            NORMAL,
            new UnmodifiableMapBuilder<SensorType, Integer>()
                .add(TRAFFIC, -10)
                .add(TRAFFIC_CLEAR, 10)
                .add(WEATHER_RAINY, -5)
                .add(WEATHER_CLEAR, 5)
                .add(SLIPPERY_ROAD, -15)
                .add(SLIPPERY_ROAD_CLEAR, 15)
                .add(EMERGENCY_TURBO, 20)
                .add(SPEED_LIMIT_SIGN, 0)
                .build()
        );
        SPEEDMAPS_BY_DRIVE_MODE.put(
            SPORT,
            new UnmodifiableMapBuilder<SensorType, Integer>()
                .add(TRAFFIC, -5)
                .add(TRAFFIC_CLEAR, 5)
                .add(WEATHER_RAINY, -5)
                .add(WEATHER_CLEAR, 5)
                .add(SLIPPERY_ROAD, -15)
                .add(SLIPPERY_ROAD_CLEAR, 15)
                .add(EMERGENCY_TURBO, 30)
                .add(SPEED_LIMIT_SIGN, 5)
                .build()
        );
        SPEEDMAPS_BY_DRIVE_MODE.put(
            SAFE,
            new UnmodifiableMapBuilder<SensorType, Integer>()
                .add(TRAFFIC, -15)
                .add(TRAFFIC_CLEAR, 15)
                .add(WEATHER_RAINY, -5)
                .add(WEATHER_CLEAR, 5)
                .add(SLIPPERY_ROAD, -15)
                .add(SLIPPERY_ROAD_CLEAR, 15)
                .add(EMERGENCY_TURBO, 10)
                .add(SPEED_LIMIT_SIGN, -5)
                .build()
        );
    }
}

interface Action extends BiFunction<SelfDriver, SensorEvent, Result<Void>> {
}

