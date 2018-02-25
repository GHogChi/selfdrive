package com.notquitehere.selfdriver.support;

public interface CliPort {
    Result<Void> tell(String format, String... args);

    Result<String> getInput();

    Result<String> ask(String prompt, String... args);

    String nextInput();
}
