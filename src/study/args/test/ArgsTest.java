package study.args.test;


import org.junit.jupiter.api.Test;
import study.args.Args;
import study.args.exception.ArgsException;

import static org.assertj.core.api.Assertions.*;

class ArgsTest {

    @Test
    void testCreateWithNoSchemaOrArguments() throws Exception {
        Args args = new Args("", new String[0]);

        assertThat(args.cardinality()).isEqualTo(0);
    }

    @Test
    void testWithNoSchemaButWithOneArgument() throws Exception {
        try {
            Args args = new Args("", new String[]{"-x"});
        } catch (ArgsException e) {
            assertThat(e.getErrorCode()).isEqualTo(ArgsException.ErrorCode.UNEXPECTED_ARGUMENT);
            assertThat(e.getErrorArgumentId()).isEqualTo('x');
        }
    }

    @Test
    void testWithNoSchemaButWithMultipleArguments() throws Exception {
        try {
            Args args = new Args("", new String[]{"-x", "-y"});
        } catch (ArgsException e) {
            assertThat(e.getErrorCode()).isEqualTo(ArgsException.ErrorCode.UNEXPECTED_ARGUMENT);
            assertThat(e.getErrorArgumentId()).isEqualTo('x');
        }
    }

    @Test
    void testWithNoSchemaButGetBooleanValue() throws Exception {
        Args args = new Args("", new String[]{});

        assertThat(args.getBoolean('b')).isFalse();
    }

    @Test
    void testWithNoSchemaButGetStringValue() throws Exception {
        Args args = new Args("", new String[]{});

        assertThat(args.getString('s')).isEqualTo("");
    }

    @Test
    void testWithSchemaButGetIntegerValue() throws Exception {
        Args args = new Args("", new String[]{});

        assertThat(args.getInt('i')).isEqualTo(0);
    }

    @Test
    void testNonLetterSchema() throws Exception {
        try {
            Args args = new Args("*", new String[]{});
        } catch (ArgsException e) {
            assertThat(e.getErrorCode()).isEqualTo(ArgsException.ErrorCode.INVALID_ARGUMENT_NAME);
            assertThat(e.getErrorArgumentId()).isEqualTo('*');
        }
    }

    @Test
    void testInvalidArgumentFormat() throws Exception {
        try {
            Args args = new Args("f~", new String[]{});
        } catch (ArgsException e) {
            assertThat(e.getErrorCode()).isEqualTo(ArgsException.ErrorCode.INVALID_FORMAT);
            assertThat(e.getErrorArgumentId()).isEqualTo('f');
        }
    }

    @Test
    void testSimpleBooleanPresent() throws Exception {
        Args args = new Args("x", new String[]{"-x"});

        assertThat(args.getBoolean('x')).isTrue();
    }

    @Test
    void testSimpleStringPresent() throws Exception {
        Args args = new Args("x*", new String[]{"-x", "param"});

        assertThat(args.getString('x')).isEqualTo("param");
    }

    @Test
    void testMissingStringArgument() throws Exception {
        try {
            Args args = new Args("x*", new String[]{"-x"});
        } catch (ArgsException e) {
            assertThat(e.getErrorCode()).isEqualTo(ArgsException.ErrorCode.MISSING_STRING);
            assertThat(e.getErrorArgumentId()).isEqualTo('x');
        }
    }

    @Test
    void testSpacesInFormat() throws Exception {
        Args args = new Args("x  ,   y", new String[]{"-xy"});
        assertThat(args.has('x')).isTrue();
        assertThat(args.has('y')).isTrue();
    }

    @Test
    void testSimpleIntegerPresent() throws Exception {
        Args args = new Args("x#", new String[]{"-x", "13"});

        assertThat(args.getInt('x')).isEqualTo(13);
    }

    @Test
    void testInvalidInteger() throws Exception {
        try {
            Args args = new Args("x#", new String[]{"-x", "Forty one"});
        } catch (ArgsException e) {
            assertThat(e.getErrorCode()).isEqualTo(ArgsException.ErrorCode.INVALID_INTEGER);
            assertThat(e.getErrorArgumentId()).isEqualTo('x');
        }
    }

    @Test
    void testMissingInteger() throws Exception {
        try {
            Args args = new Args("x#", new String[]{"-x"});
        } catch (ArgsException e) {
            assertThat(e.getErrorCode()).isEqualTo(ArgsException.ErrorCode.MISSING_INTEGER);
            assertThat(e.getErrorArgumentId()).isEqualTo('x');
        }
    }

    @Test
    void testSimpleDoublePresent() throws Exception {
        Args args = new Args("x##", new String[]{"-x", "53.4"});

        assertThat(args.getDouble('x')).isEqualTo(53.4);
    }

    @Test
    void testInvalidDouble() throws Exception {
        try {
            Args args = new Args("x##", new String[]{"-x", "53,4"});
        } catch (ArgsException e) {
            assertThat(e.getErrorCode()).isEqualTo(ArgsException.ErrorCode.INVALID_DOUBLE);
            assertThat(e.getErrorArgumentId()).isEqualTo('x');
        }

    }

    @Test
    void testMissingDouble() throws Exception {
        try {
            Args args = new Args("x##", new String[]{"-x"});
        } catch (ArgsException e) {
            assertThat(e.getErrorCode()).isEqualTo(ArgsException.ErrorCode.MISSING_DOUBLE);
            assertThat(e.getErrorArgumentId()).isEqualTo('x');
        }
    }

    @Test
    void testMultipleParameterPresent() throws Exception {
        Args args = new Args("b,i#,s*", new String[]{"-b", "-i", "30", "-s", "test"});
        assertThat(args.getBoolean('b')).isTrue();
        assertThat(args.getInt('i')).isEqualTo(30);
        assertThat(args.getString('s')).isEqualTo("test");
    }
}