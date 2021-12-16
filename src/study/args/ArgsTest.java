package study.args;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ArgsTest {

    @Test
    void testCreateWithNoSchemaOrArguments() throws Exception {
        Args args = new Args("", new String[0]);
        assertThat(args.cardinality()).isEqualTo(0);
    }

    @Test
    void testWithNoSchemaButWithOneArgument() throws Exception {
        Args args = new Args("", new String[]{"-x"});
        assertThat(args.isValid()).isFalse();
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
    void testSimpleIntegerPresent() throws Exception {
        Args args = new Args("x#", new String[]{"-x", "13"});
        assertThat(args.isValid()).isTrue();
        assertThat(args.getInt('x')).isEqualTo(13);
    }

    @Test
    void testMultipleParameterPresent() throws Exception {
        Args args = new Args("b,i#,s*", new String[]{"-b", "-i", "30", "-s", "test"});
        assertThat(args.isValid()).isTrue();
        assertThat(args.getBoolean('b')).isTrue();
        assertThat(args.getInt('i')).isEqualTo(30);
        assertThat(args.getString('s')).isEqualTo("test");
    }

}