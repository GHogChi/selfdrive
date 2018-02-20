package com.notquitehere.selfdriver;

import com.notquitehere.selfdriver.util.Result;
import com.notquitehere.selfdriver.util.UnmodifiableMapBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static com.notquitehere.selfdriver.DriveMode.*;
import static com.notquitehere.selfdriver.SensorType.*;
import static com.notquitehere.selfdriver.SpeedLimit.forceFit;
import static com.notquitehere.selfdriver.util.Result.success;

public class SelfDriver {
    private DriverState state;

    public static final int STARTING_SPEED = 20;
    public static final int MIN_SPEED = 10;

    public boolean isInTraffic() {
        return state.inTraffic;
    }

    public void setInTraffic(boolean inTraffic) {
        state = state.setTraffic(inTraffic);
    }

    public boolean isInRain() {
        return state.inRain;
    }

    public void setInRain(boolean inRain) {
        state.setRain(inRain);
    }

    public boolean isOnSlipperyRoad() {
        return state.onSlipperyRoad;
    }

    public void setOnSlipperyRoad(boolean onSlipperyRoad) {
        state.setSlipperyRoad(onSlipperyRoad);
    }

    public boolean turboIsOn() {
        return state.turboOn;
    }

    public int turboUseCount(){
        return state.turboUseCount;
    }

    public void setSpeed(int speed) {
        state = state.setSpeed(forceFit(speed));
    }

    public void adjustSpeed(int delta){
        state = state.setSpeed(
            forceFit(Math.max(state.speed + delta, MIN_SPEED)));
    }

    public DriveMode mode() { return state.mode; }

    public int speed() {
        return state.speed;
    }

    public static final Map<DriveMode, Map<SensorType, Integer>>
        speedMapsByDriveMode;

    private static final Map<SensorType, Action> actionsByType;

    static {
        speedMapsByDriveMode = new HashMap<>();
        buildSpeedMaps();
        actionsByType = new HashMap<>();
        actionsByType.put(TRAFFIC, SelfDriver::handleTraffic);
        actionsByType.put(TRAFFIC_CLEAR, SelfDriver::handleTrafficClear);
        actionsByType.put(WEATHER_RAINY, SelfDriver::handleWeatherRainy);
        actionsByType.put(WEATHER_CLEAR, SelfDriver::handleWeatherClear);
        actionsByType.put(SLIPPERY_ROAD, SelfDriver::handleSlipperyRoad);
        actionsByType.put(SLIPPERY_ROAD_CLEAR,
            SelfDriver::handleSlipperyRoadClear);
        actionsByType.put(EMERGENCY_TURBO, SelfDriver::handleTurbo);
        actionsByType.put(SPEED_LIMIT_SIGN, SelfDriver::handleSpeedLimit);

    }

    public SelfDriver(DriveMode mode) {
        state = new DriverState(
            mode, STARTING_SPEED, false, false, false, false,
            0);
    }

    public Result<Void> handleSensorEvent(SensorEvent event) {
        Action action = actionsByType.get(event.type);
        return action.apply(this, event);
    }

    private static Result<Void> handleTraffic(SelfDriver sd, SensorEvent e) {
        sd.setInTraffic(true);
        sd.adjustSpeed(getDelta(sd, TRAFFIC));
        return success();
    }

    private static Result<Void> handleTrafficClear(SelfDriver sd, SensorEvent e){
        sd.adjustSpeed(getDelta(sd, TRAFFIC_CLEAR));
        return success();
    }

    private static Result<Void> handleWeatherRainy(SelfDriver sd,
                                                   SensorEvent e) {
        sd.adjustSpeed(getDelta(sd, WEATHER_RAINY));
        return success();
    }

    private static Result<Void> handleWeatherClear(SelfDriver sd,
                                                   SensorEvent e) {
        sd.adjustSpeed(getDelta(sd, WEATHER_CLEAR));
        return success();
    }

    private static Result<Void> handleSlipperyRoad(SelfDriver sd,
                                                   SensorEvent e) {
        return null;
    }

    private static Result<Void> handleSlipperyRoadClear(SelfDriver sd,
                                                        SensorEvent e) {
        return null;
    }

    private static Result<Void> handleTurbo(SelfDriver sd, SensorEvent e) {
        if (sd.turboUseCount() == 0) {
            sd.startTurbo(getDelta(sd, EMERGENCY_TURBO));
        }
        return success();
    }

    private static Result<Void> handleSpeedLimit(SelfDriver sd, SensorEvent e) {
        int limit = ((SpeedLimit) e.parameter).limit;
        sd.setSpeed(limit + getDelta(sd, SPEED_LIMIT_SIGN));
        return success();
    }

    private void startTurbo(Integer speedDelta) {
        state = state.setTurbo(true, state.speed + speedDelta);
    }

    private static Integer getDelta(SelfDriver sd, SensorType sensorType) {
        return speedMapsByDriveMode.get(sd.state.mode).get(sensorType);
    }

    //todo load from files, env vars, DB or ...
    private static void buildSpeedMaps() {
        speedMapsByDriveMode.put(
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
        speedMapsByDriveMode.put(
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
        speedMapsByDriveMode.put(
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

