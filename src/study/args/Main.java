package study.args;

import java.text.ParseException;

public class Main {

    public static void main(String[] args) {
        try {
            Args arguments = new Args("b,i#,s*", args);
            boolean b = arguments.getBoolean('b');
            int i = arguments.getInt('i');
            String s = arguments.getString('s');
            System.out.printf("%b %d %s%n", b, i, s);
        } catch (ArgsException e) {
            System.out.println("e.getMessage() = " + e.getMessage());
        }
    }
}
