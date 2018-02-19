package com.notquitehere.selfdriver.util;

import static com.notquitehere.selfdriver.util.Result.success;

public class Speed {
    public final int speed;

    public Speed(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Speed must be non-negative");
        }
        speed = value;
    }

    public Result<Speed> changeBy(Integer delta){
        int rawChangedValue = speed + delta;
        return success(new Speed((rawChangedValue >= 0) ? rawChangedValue : 0));
    }
}
