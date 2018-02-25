package com.notquitehere.selfdriver.domain;

class Expectation {

    public final SensorEvent event;
    public final Integer expectedSpeed;

    protected Expectation(SensorEvent event, Integer expectedSpeed) {
        this.event = event;
        this.expectedSpeed = expectedSpeed;
    }

    public static Expectation expect(SensorEvent e, Integer s) {
        return new Expectation(e, s);
    }

}
