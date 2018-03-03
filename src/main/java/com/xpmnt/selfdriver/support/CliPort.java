package com.xpmnt.selfdriver.support;

public interface CliPort {
    Result<Void> tell(String format, Object... args);

    Result<String> getInput();

    Result<String> ask(String prompt, Object... args);

    String nextInput();
}
