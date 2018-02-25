package com.notquitehere.selfdriver.support;

import java.io.Console;

import static com.notquitehere.selfdriver.support.Result.failure;
import static com.notquitehere.selfdriver.support.Result.success;

//todo add Optional(console) as constructor arg? - simplifies mock injection
public class DefaultCliPort implements CliPort {
    private Console console = System.console();

    private DefaultCliPort() { }

    @Override
    public Result<Void> tell(String format, String... args) {
        try {
            console.format(format + "\n", (String[])args);
            return success();
        } catch (Throwable e) {
            return failure(e.getLocalizedMessage());
        }
    }

    @Override
    public Result<String> getInput() {
        try {
            return success(nextInput());
        } catch (Throwable e) {
            return failure(e.getLocalizedMessage());
        }
    }

    @Override
    public Result<String> ask(String prompt, String... args) {
        try {
            String response = console.readLine(prompt, (String[])args);
            return success(response);
        } catch (Throwable e) {
            return failure(e.getLocalizedMessage());
        }
    }

    @Override
    public String nextInput() {
        return console.readLine();
    }

    public static Result<CliPort> create() {
        try {
            return success(new DefaultCliPort());
        } catch (Throwable e) {
            return failure(e.getLocalizedMessage());
        }
    }
}
