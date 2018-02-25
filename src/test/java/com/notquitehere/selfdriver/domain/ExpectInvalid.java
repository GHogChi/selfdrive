package com.notquitehere.selfdriver.domain;

class ExpectInvalid extends Expectation {

    private ExpectInvalid(SensorEvent event) {
        super(event, 0);
    }

    public static ExpectInvalid expectInvalid(SensorEvent e) {
        return new ExpectInvalid(e);
    }
}
