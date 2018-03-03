package com.xpmnt.selfdriver.support;

import java.io.Console;

import static com.xpmnt.selfdriver.support.Result.failure;
import static com.xpmnt.selfdriver.support.Result.success;

//todo add Optional(console) as constructor arg? - simplifies mock injection
public class DefaultCliPort implements CliPort {
    private Console console = System.console();

    private DefaultCliPort() { }

    @Override
    public Result<Void> tell(String format, Object... args) {
        try {
            console.format(format + "\n", args);
            return Result.success();
        } catch (Throwable e) {
            return Result.failure(e.getLocalizedMessage());
        }
    }

    @Override
    public Result<String> getInput() {
        try {
            return Result.success(nextInput());
        } catch (Throwable e) {
            return Result.failure(e.getLocalizedMessage());
        }
    }

    @Override
    public Result<String> ask(String prompt, Object... args) {
        try {
            String response = console.readLine(prompt, args);
            return Result.success(response);
        } catch (Throwable e) {
            return Result.failure(e.getLocalizedMessage());
        }
    }

    @Override
    public String nextInput() {
        return console.readLine();
    }

    public static Result<CliPort> create() {
        try {
            return Result.success(new DefaultCliPort());
        } catch (Throwable e) {
            return Result.failure(e.getLocalizedMessage());
        }
    }
}
