package study.args;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static study.args.ArgsException.ErrorCode.*;

public class ArgsExceptionTest {

    @Test
    void testUnexpectedMessage() throws Exception {
        ArgsException e = new ArgsException(UNEXPECTED_ARGUMENT, 'x', null);
        assertThat(e.errorMessage()).isEqualTo("Argument -x unexpected.");
    }

    @Test
    void testMissingStringMessage() throws Exception {
        ArgsException e = new ArgsException(MISSING_STRING, 'x', null);
        assertThat(e.errorMessage()).isEqualTo("Could not find string parameter for -x.");
    }

    @Test
    void testInvalidIntegerMessage() throws Exception {
        ArgsException e = new ArgsException(INVALID_INTEGER, 'x', "Forty Three");
        assertThat(e.errorMessage()).isEqualTo("Argument -x expects an integer but was 'Forty Three'.");
    }

    @Test
    void testMissingIntegerMessage() throws Exception {
        ArgsException e = new ArgsException(MISSING_INTEGER, 'x', null);
        assertThat(e.errorMessage()).isEqualTo("Could not find integer parameter for -x.");
    }

    @Test
    void testInvalidDoubleMessage() throws Exception {
        ArgsException e = new ArgsException(INVALID_DOUBLE, 'x', "51,4");
        assertThat(e.errorMessage()).isEqualTo("Argument -x expects an double but was '51,4'.");
    }

    @Test
    void testMissingDoubleMessage() throws Exception {
        ArgsException e = new ArgsException(MISSING_DOUBLE, 'x', null);
        assertThat(e.errorMessage()).isEqualTo("Could not find double parameter for -x.");
    }
}
