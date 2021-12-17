package study.args;


import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.assertj.core.api.Assertions.*;

class ArgsTest {

    @Test
    void testCreateWithNoSchemaOrArguments() throws Exception {
        Args args = new Args("", new String[0]);

        assertThat(args.isValid()).isTrue();
        assertThat(args.cardinality()).isEqualTo(0);
    }

    @Test
    void testWithNoSchemaButWithOneArgument() throws Exception {
        Args args = new Args("", new String[]{"-x"});

        fail(args, "Argument(s) -x unexpected.");
    }

    @Test
    void testWithNoSchemaButWithMultipleArguments() throws Exception {
        Args args = new Args("", new String[]{"-x", "-y"});

        fail(args, "Argument(s) -xy unexpected.");
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
        assertThatThrownBy(() -> new Args("*", new String[]{}))
                .isInstanceOf(ParseException.class);
    }

    @Test
    void testInvalidArgumentFormat() throws Exception {
        assertThatThrownBy(() -> new Args("f~", new String[]{}))
                .isInstanceOf(ParseException.class);
    }

    @Test
    void testSimpleBooleanPresent() throws Exception {
        Args args = new Args("x", new String[]{"-x"});
        assertThat(args.isValid()).isTrue();
        assertThat(args.getBoolean('x')).isTrue();
    }

    @Test
    void testSimpleStringPresent() throws Exception {
        Args args = new Args("x*", new String[]{"-x", "param"});
        assertThat(args.isValid()).isTrue();
        assertThat(args.getString('x')).isEqualTo("param");
    }

    @Test
    void testMissingStringArgument() throws Exception {
        Args args = new Args("x*", new String[]{"-x"});

        fail(args, "Could not find string parameter for -x.");
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
        assertThat(args.isValid()).isTrue();
        assertThat(args.getInt('x')).isEqualTo(13);
    }

    @Test
    void testInvalidInteger() throws Exception {
        Args args = new Args("x#", new String[]{"-x", "Forty one"});

        fail(args, "Argument -x expects an integer but was 'TILT'.");
    }

    @Test
    void testMissingInteger() throws Exception {
        Args args = new Args("x#", new String[]{"-x"});

        fail(args, "Could not find integer parameter for -x.");
    }

    @Test
    void testSimpleDoublePresent() throws Exception {
        Args args = new Args("x##", new String[]{"-x", "53.4"});

        assertThat(args.isValid()).isTrue();
        assertThat(args.getDouble('x')).isEqualTo(53.4);
    }

    @Test
    void testInvalidDouble() throws Exception {
        Args args = new Args("x##", new String[]{"-x", "53,4"});

        fail(args, "Argument -x expects an double but was '53,4'.");
    }

    @Test
    void testMissingDouble() throws Exception {
        Args args = new Args("x##", new String[]{"-x"});

        fail(args, "Could not find double parameter for -x.");
    }

    @Test
    void testMultipleParameterPresent() throws Exception {
        Args args = new Args("b,i#,s*", new String[]{"-b", "-i", "30", "-s", "test"});
        assertThat(args.isValid()).isTrue();
        assertThat(args.getBoolean('b')).isTrue();
        assertThat(args.getInt('i')).isEqualTo(30);
        assertThat(args.getString('s')).isEqualTo("test");
    }

    private void fail(Args args, String message) {
        assertThat(args.isValid()).isFalse();
        assertThat(args.cardinality()).isEqualTo(0);
        try {
            assertThat(args.errorMessage()).isEqualTo(message);
        } catch (Exception e) {
        }
    }

}