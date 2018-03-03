package com.xpmnt.selfdriver.support;

import com.xpmnt.selfdriver.domain.SensorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.xpmnt.selfdriver.support.Result.success;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

public class WhenSelfDriverIsRunFromCommandLine {

    @Mock
    CliPort cliPort;

    public static final String MODE_PROMPT = "Enter drive mode: ";

    @BeforeEach
    public void setup() {
        initMocks(this);
    }

    @Test
    public void speedLimitEventIsRespondedToCorrectly() {
        Integer[] inputs = {50, SensorType.PARK_AND_SHUT_DOWN.id};
        FakeCliPort port = new FakeCliPort();
        port.setInputs(prep(inputs));
        final DefaultInputAdapter inputAdapter =
            DefaultInputAdapter.create("NORMAL").getOutput();

        new CommandLine(port, inputAdapter).run();

        assertTrue(port.outputs.size() > 0);
    }

    private String[] prep(Integer... vals) {
        return Arrays.stream(vals).map(Object::toString).toArray(String[]::new);
    }

}
class FakeCliPort implements CliPort {
    public final List<String> outputs = new ArrayList<>();
    private String[] inputs;
    int inputIndex = 0;

    public void setInputs(String[] inputs){

        this.inputs = inputs;
    }

    @Override
    public Result<Void> tell(String format, Object... args) {
        outputs.add(String.format(format, args));
        return Result.success();
    }

    @Override
    public Result<String> getInput() {
        return Result.success(nextInput());
    }

    @Override
    public Result<String> ask(String prompt, Object... args) {
        final String input = nextInput();
        return Result.success(input);
    }

    @Override
    public String nextInput() {
        return (inputIndex < inputs.length)
                ? inputs[inputIndex++] : SensorType.PARK_AND_SHUT_DOWN.name;
    }

}