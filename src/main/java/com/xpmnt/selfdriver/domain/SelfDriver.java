package com.xpmnt.selfdriver.domain;

import com.xpmnt.selfdriver.support.Result;
import com.xpmnt.selfdriver.support.UnmodifiableMapBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static com.xpmnt.selfdriver.support.Result.success;

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

    public boolean isRaining() {
        return state.isRaining;
    }

    public void setRaining(boolean inRain) {
        state = state.setRaining(inRain);
    }

    public boolean isOnSlipperyRoad() {
        return state.onSlipperyRoad;
    }

    public void setOnSlipperyRoad(boolean onSlipperyRoad) {
        state = state.setSlipperyRoad(onSlipperyRoad);
    }

    public boolean turboIsOn() {
        return state.turboOn;
    }

    public int turboUseCount(){
        return state.turboUseCount;
    }

    public void setSpeed(int speed) {
        state = state.setSpeed(SpeedLimit.forceFit(speed));
    }

    public void adjustSpeed(int delta){
        final int max = Math.max(state.speed + delta, MIN_SPEED);
        state = state.setSpeed(SpeedLimit.forceFit(max));
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
        actionsByType.put(SensorType.TRAFFIC, (sd1, e1) -> handleTraffic(sd1));
        actionsByType.put(SensorType.TRAFFIC_CLEAR, (sd1, e1) -> handleTrafficClear(sd1));
        actionsByType.put(SensorType.WEATHER_RAINY, (sd1, e1) -> handleWeatherRainy(sd1));
        actionsByType.put(SensorType.WEATHER_CLEAR, (sd1, e1) -> handleWeatherClear(sd1));
        actionsByType.put(SensorType.SLIPPERY_ROAD, (sd1, e1) -> handleSlipperyRoad(sd1));
        actionsByType.put(SensorType.SLIPPERY_ROAD_CLEAR,
            (sd1, e1) -> handleSlipperyRoadClear(sd1));
        actionsByType.put(SensorType.EMERGENCY_TURBO, (sd, e) -> handleTurbo(sd));
        actionsByType.put(SensorType.SPEED_LIMIT_SIGN, SelfDriver::handleSpeedLimit);

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

    private static Result<Void> handleTraffic(SelfDriver sd) {
        if (!sd.isInTraffic()) {
            sd.setInTraffic(true);
            sd.adjustSpeed(getDelta(sd, SensorType.TRAFFIC));
        }
        return Result.success();
    }

    private static Result<Void> handleTrafficClear(SelfDriver sd){
        if (sd.isInTraffic()) {
            sd.adjustSpeed(getDelta(sd, SensorType.TRAFFIC_CLEAR));
            sd.setInTraffic(false);
        }
        return Result.success();
    }

    private static Result<Void> handleWeatherRainy(SelfDriver sd) {

        if (!sd.isRaining()) {
            sd.adjustSpeed(getDelta(sd, SensorType.WEATHER_RAINY));
            sd.setRaining(true);
        }
        return Result.success();
    }

    private static Result<Void> handleWeatherClear(SelfDriver sd) {
        if (sd.isRaining()) {
            sd.adjustSpeed(getDelta(sd, SensorType.WEATHER_CLEAR));
            sd.setRaining(false);
        }
        return Result.success();
    }

    private static Result<Void> handleSlipperyRoad(SelfDriver sd) {

        if (!sd.isOnSlipperyRoad()) {
            sd.setOnSlipperyRoad(true);
            sd.adjustSpeed(getDelta(sd, SensorType.SLIPPERY_ROAD));
        }
        return Result.success();
    }

    private static Result<Void> handleSlipperyRoadClear(SelfDriver sd) {
        if(sd.isOnSlipperyRoad()){
            sd.setOnSlipperyRoad(false);
            sd.adjustSpeed(getDelta(sd, SensorType.SLIPPERY_ROAD_CLEAR));
        }
        return Result.success();
    }

    private static Result<Void> handleTurbo(SelfDriver sd) {
        if (sd.turboUseCount() == 0 && !sd.isOnSlipperyRoad()) {
            sd.startTurbo(getDelta(sd, SensorType.EMERGENCY_TURBO));
        }
        return Result.success();
    }

    private static Result<Void> handleSpeedLimit(SelfDriver sd, SensorEvent e) {
        int limit = ((SpeedLimit) e.parameter).limit;
        sd.setSpeed(limit + getDelta(sd, SensorType.SPEED_LIMIT_SIGN));
        return Result.success();
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
            DriveMode.NORMAL,
            new UnmodifiableMapBuilder<SensorType, Integer>()
                .add(SensorType.TRAFFIC, -10)
                .add(SensorType.TRAFFIC_CLEAR, 10)
                .add(SensorType.WEATHER_RAINY, -5)
                .add(SensorType.WEATHER_CLEAR, 5)
                .add(SensorType.SLIPPERY_ROAD, -15)
                .add(SensorType.SLIPPERY_ROAD_CLEAR, 15)
                .add(SensorType.EMERGENCY_TURBO, 20)
                .add(SensorType.SPEED_LIMIT_SIGN, 0)
                .build()
        );
        speedMapsByDriveMode.put(
            DriveMode.SPORT,
            new UnmodifiableMapBuilder<SensorType, Integer>()
                .add(SensorType.TRAFFIC, -5)
                .add(SensorType.TRAFFIC_CLEAR, 5)
                .add(SensorType.WEATHER_RAINY, -5)
                .add(SensorType.WEATHER_CLEAR, 5)
                .add(SensorType.SLIPPERY_ROAD, -15)
                .add(SensorType.SLIPPERY_ROAD_CLEAR, 15)
                .add(SensorType.EMERGENCY_TURBO, 30)
                .add(SensorType.SPEED_LIMIT_SIGN, 5)
                .build()
        );
        speedMapsByDriveMode.put(
            DriveMode.SAFE,
            new UnmodifiableMapBuilder<SensorType, Integer>()
                .add(SensorType.TRAFFIC, -15)
                .add(SensorType.TRAFFIC_CLEAR, 15)
                .add(SensorType.WEATHER_RAINY, -5)
                .add(SensorType.WEATHER_CLEAR, 5)
                .add(SensorType.SLIPPERY_ROAD, -15)
                .add(SensorType.SLIPPERY_ROAD_CLEAR, 15)
                .add(SensorType.EMERGENCY_TURBO, 10)
                .add(SensorType.SPEED_LIMIT_SIGN, -5)
                .build()
        );
    }
}

interface Action extends BiFunction<SelfDriver, SensorEvent, Result<Void>> {
}

