package com.notquitehere.selfdriver.util;


import java.io.Serializable;

/**
 * Keep these in ascending order of severity, or add a field.
 */
public enum Severity implements Serializable {
    OKAY, WARNING, FLESH_WOUND, FATAL
}
