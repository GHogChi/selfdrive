package com.xpmnt.selfdriver.support;

import static com.xpmnt.selfdriver.support.Result.success;

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
        return Result
            .success(new Speed((rawChangedValue >= 0) ? rawChangedValue : 0));
    }
}
