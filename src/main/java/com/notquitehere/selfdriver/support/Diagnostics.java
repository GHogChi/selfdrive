package com.notquitehere.selfdriver.support;

public class Diagnostics {
    public static void dump(String format, int... args) {
        System.out.println(String.format("[" + format + "]", args));
    }
}
