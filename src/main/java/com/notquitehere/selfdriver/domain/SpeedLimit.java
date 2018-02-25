package com.notquitehere.selfdriver.domain;

public class SpeedLimit {
    public final int limit;
    public static final int LOWER_BOUND = 10;
    public static final int UPPER_BOUND = 100;

    public SpeedLimit(int limit) {
        if (limit < LOWER_BOUND || limit > UPPER_BOUND) {
            throw new IllegalArgumentException(
                String.format("Limit must be >= %d and <= %d",
                    LOWER_BOUND,
                    UPPER_BOUND)
            );
        }
        this.limit = limit;
    }

    public static int forceFit(int speed) {
        return (speed < LOWER_BOUND) ? LOWER_BOUND :
                (speed > UPPER_BOUND) ? UPPER_BOUND :
                speed;
    }

    public static boolean speedIsValid(int speed){
        return LOWER_BOUND <= speed && speed <= UPPER_BOUND;
    }

    public static SpeedLimit limit(int limit) {
        return new SpeedLimit(limit);
    }
}
