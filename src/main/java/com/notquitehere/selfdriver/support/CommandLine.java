package com.notquitehere.selfdriver.support;

import java.util.Arrays;
import java.util.List;

import static com.notquitehere.selfdriver.support.Result.failure;
import static com.notquitehere.selfdriver.support.Result.success;

/**
 * AHA 0.1 Ujoint First: based on Presenter First
 * Ujoint: CommandLine
 * Adapter: InputAdapter
 * Port: CliPort
 *
 * @see
 * <a href="https://en.wikipedia.org/wiki/Presenter_first_(software_approach)">
 * Presenter First</a>
 */
public class CommandLine {
    private final InputAdapter adapter;
    private final CliPort port;
    private static final List<String> helpKeys =
        Arrays.asList("help", "?", "-h", "--help");


    CommandLine(CliPort port, InputAdapter adapter) {
        this.port = port;
        this.adapter = adapter;
        port.tell("Welcome. Driving mode is %s", adapter.modeName());
        port.tell("" + adapter.speed());
    }

    public static void main(String[] args) {
        final CliPort c = createCliPort();
        final InputAdapter adapter = createInputAdapter(args, c);
        new CommandLine(c, adapter).run();
    }

    public Result<Void> run() {
        try {
            do {
                Result<String> inputResult = port.ask("Enter event: ");
                if (inputResult.succeeded()) {
                    final String input = inputResult.getOutput();
                    if (helpKeys.contains(input.toLowerCase())) {
                        displayUsage();
                        continue;
                    }
                    Result<String> handlerResult =
                        adapter.handleInput(input);
                    if (handlerResult.succeeded()) {
                        final String output = handlerResult.getOutput();
                        port.tell(output);
                    } else {
                        port.tell(handlerResult.getErrorMessage());
                    }
                }
            } while (adapter.isAcceptingInput());
        } catch (Exception e) {
            final String errorMsg = e.getLocalizedMessage();
            port.tell(errorMsg);
            return failure(errorMsg);
        }
        return success();
    }

    private void displayUsage() {
        String[] usage = adapter.usage();
        for (String line : usage) {
            port.tell(line);
        }
    }

    private static CliPort createCliPort() {
        Result<CliPort> result = DefaultCliPort.create();
        if (result.failed()) {
            System.err.println(result.getErrorMessage());
            System.exit(1);
        }
        return result.getOutput();
    }

    private static InputAdapter createInputAdapter(String[] args, CliPort c) {
        Result<DefaultInputAdapter> result = DefaultInputAdapter.create(
            (args.length > 0) ? args[0] : ""
        );
        if (result.failed()) {
            c.tell(result.getErrorMessage());
            System.exit(1);
        }
        return result.getOutput();
    }
}
