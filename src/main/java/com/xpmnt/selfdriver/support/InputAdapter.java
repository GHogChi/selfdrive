package com.xpmnt.selfdriver.support;

import java.util.function.Function;

public interface InputAdapter {
    Result<String> run();

    Function<String, Result<String>> getInputHandler();

    int speed();

    boolean isAcceptingInput();

    Result<String> handleInput(String eventId);

    String modeName();

    String[] usage();
}
