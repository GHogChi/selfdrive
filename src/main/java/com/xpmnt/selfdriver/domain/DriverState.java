package com.xpmnt.selfdriver.domain;

class DriverState {
    public final DriveMode mode;
    public final int speed;
    public final boolean inTraffic;
    public final boolean isRaining;
    public final boolean onSlipperyRoad;
    public final boolean turboOn;
    public final int turboUseCount;

    public DriverState(DriveMode mode, int speed, boolean inTraffic,
                       boolean inRain, boolean onSlipperyRoad, boolean turboOn,
                       int turboUseCount) {
        this.mode = mode;
        this.speed = speed;
        this.inTraffic = inTraffic;
        this.isRaining = inRain;
        this.onSlipperyRoad = onSlipperyRoad;
        this.turboOn = turboOn;
        this.turboUseCount = turboUseCount;
    }


    public DriverState setTraffic(boolean inTraffic) {
        return new DriverState(
            mode, speed, inTraffic, isRaining, onSlipperyRoad, turboOn,
            turboUseCount);
    }

    public DriverState setRaining(boolean inRain) {
        return new DriverState(
            mode, speed, inTraffic, inRain, onSlipperyRoad, turboOn,
            turboUseCount);
    }

    public DriverState setSlipperyRoad(boolean onSlipperyRoad) {
        return new DriverState(
            mode, speed, inTraffic, isRaining, onSlipperyRoad, turboOn,
            turboUseCount);
    }

    public DriverState setTurbo(boolean turboOn, int speedToSet) {
        return new DriverState(
            mode, speedToSet, inTraffic, isRaining, onSlipperyRoad, turboOn,
            turboUseCount + ((turboOn) ? 1 : 0));
    }

    public DriverState setSpeed(int speed) {
        return new DriverState(
            mode, speed, inTraffic, isRaining, onSlipperyRoad, turboOn,
            turboUseCount);
    }
}
