package study.args;

import java.text.ParseException;
import java.util.*;

/**
 * BEFORE REFACTOR
 */

public class Args {

    private String schema;
    private String[] args;
    private boolean valid = true;
    private Set<Character> unexpectedArguments = new TreeSet<>();
    private Map<Character, ArgumentMarshaller> marshaller = new HashMap<>();
    private Set<Character> argsFound = new HashSet<>();
    private int currentArgument;
    private char errorArgumentId = '\0';
    private String errorParameter = "TILT";
    private ErrorCode errorCode = ErrorCode.OK;

    private enum ErrorCode {
        OK, MISSING_STRING, MISSING_INTEGER, INVALID_INTEGER, UNEXPECTED_ARGUMENT
    }

    public Args(String schema, String[] args) throws ParseException {
        this.schema = schema;
        this.args = args;
        valid = parse();
    }

    private boolean parse() throws ParseException {

        if (schema.length() == 0 && args.length == 0) {
            return true;
        }

        parseSchema();
        try {
            parseArguments();
        } catch (ArgsException e) {
        }
        return valid;
    }

    private boolean parseSchema() throws ParseException {
        for (String element : schema.split(",")) {
            if (element.length() > 0) {
                String trimmedElement = element.trim();
                parseSchemaElement(trimmedElement);
            }
        }
        return true;
    }

    private void parseSchemaElement(String element) throws ParseException {
        char elementId = element.charAt(0);
        String elementTail = element.substring(1);
        validateSchemaElementId(elementId);
        if (isBooleanSchemaElement(elementTail)) {
            marshaller.put(elementId, new BooleanArgumentMarshaller());
        } else if (isStringSchemaElement(elementTail)) {
            marshaller.put(elementId, new StringArgumentMarshaller());
        } else if (isIntegerSchemaElement(elementTail)) {
            marshaller.put(elementId, new IntegerArgumentMarshaller());
        } else {
            throw new ParseException(String.format("Argument: %c has invalid format: %s.", elementId, elementTail), 0);
        }
    }



    private void validateSchemaElementId(char elementId) throws ParseException {
        if (!Character.isLetter(elementId)) {
            throw new ParseException("Bad character:" + elementId + "in Args format: " + schema, 0);
        }
    }

    private boolean isBooleanSchemaElement(String elementTail) {
        return elementTail.length() == 0;
    }
    private boolean isIntegerSchemaElement(String elementTail) {
        return elementTail.equals("#");
    }
    private boolean isStringSchemaElement(String elementTail) {
        return elementTail.equals("*");
    }

    private boolean parseArguments() throws ArgsException {
        for (int currentArgument = 0; currentArgument < args.length; currentArgument++) {
            String arg = args[currentArgument];
            parseArguments(arg);
        }
        return true;
    }

    private void parseArguments(String arg) throws ArgsException {
        if (arg.startsWith("-")) {
            parseElements(arg);
        }
    }

    private void parseElements(String arg) throws ArgsException {
        for (int i = 1; i < arg.length(); i++) {
            parseElement(arg.charAt(i));
            currentArgument++;
        }
    }

    private void parseElement(char argChar) throws ArgsException {
        if (setArgument(argChar)) {
            argsFound.add(argChar);
        } else {
            unexpectedArguments.add(argChar);
            errorCode = ErrorCode.UNEXPECTED_ARGUMENT;
            valid = false;
        }
    }

    private boolean setArgument(char argChar) throws ArgsException {
        ArgumentMarshaller m = marshaller.get(argChar);
        try {
            if (m instanceof BooleanArgumentMarshaller) {
                setBooleanArg(m);
            } else if (m instanceof StringArgumentMarshaller) {
                setStringArg(m);
            } else if (m instanceof IntegerArgumentMarshaller) {
                setIntArg(m);
            } else {
                return false;
            }
        } catch (ArgsException e) {
            valid = false;
            errorArgumentId = argChar;
            throw e;
        }
        return true;
    }

    private void setIntArg(ArgumentMarshaller<Integer> m) throws ArgsException {
        currentArgument++;
        String parameter = null;
        try {
            parameter = args[currentArgument];
            m.set(Integer.valueOf(parameter));
        } catch (ArrayIndexOutOfBoundsException e) {
            errorCode = ErrorCode.MISSING_INTEGER;
            throw new ArgsException();
        } catch (NumberFormatException e) {
            errorCode = ErrorCode.INVALID_INTEGER;
            throw new ArgsException();
        }
    }

    private void setStringArg(ArgumentMarshaller<String> m) throws ArgsException {
        currentArgument++;
        try {
            m.set(args[currentArgument]);
        } catch (ArrayIndexOutOfBoundsException e) {
            errorCode = ErrorCode.MISSING_STRING;
            throw new ArgsException();
        }
    }

    private void setBooleanArg(ArgumentMarshaller<Boolean> m) {
        m.set(true);
    }

    public int cardinality() {
        return argsFound.size();
    }

    public String usage() {
        if (schema.length() > 0) {
            return "-[" + schema + "]";
        } else {
            return "";
        }
    }

    public String errorMessage() throws Exception {
        switch (errorCode) {
            case OK:
                throw new Exception("TILT: Should not get here.");
            case UNEXPECTED_ARGUMENT:
                return unexpectedArgumentsMessage();
            case MISSING_STRING:
                return String.format("Could not find string parameter for -%c.", errorArgumentId);
            case INVALID_INTEGER:
                return String.format("Argument -%c expects an integer but was '%s'.", errorArgumentId, errorParameter);
            case MISSING_INTEGER:
                return String.format("Could not find integer parameter for -%c.", errorArgumentId);
        }
        return "";
    }

    private String unexpectedArgumentsMessage() {
        StringBuffer message = new StringBuffer("Argument(s) -");
        for (Character c : unexpectedArguments) {
            message.append(c);
        }
        message.append(" unexpected.");

        return message.toString();
    }

    public String getString(char arg) {
        ArgumentMarshaller<String> am = marshaller.get(arg);
        return am == null ? "" : am.get();
    }

    public int getInt(char arg) {
        ArgumentMarshaller<Integer> am = marshaller.get(arg);
        return am == null ? 0 : am.get();
    }

    public boolean getBoolean(char arg) {
        ArgumentMarshaller<Boolean> am = marshaller.get(arg);
        return am != null && am.get();
    }

    public boolean has(char arg) {
        return argsFound.contains(arg);
    }

    public boolean isValid() {
        return valid;
    }

    private class ArgsException extends Exception {

    }

    private interface ArgumentMarshaller<T> {
        T get();

        void set(T value);

    }

    private class BooleanArgumentMarshaller implements ArgumentMarshaller<Boolean> {
        private boolean booleanValue = false;
        @Override
        public Boolean get() {
            return booleanValue;
        }

        @Override
        public void set(Boolean value) {
            booleanValue = value;
        }
    }

    private class StringArgumentMarshaller implements ArgumentMarshaller<String> {
        private String stringValue = "";

        @Override
        public String get() {
            return stringValue;
        }

        @Override
        public void set(String value) {
            this.stringValue = value;
        }
    }

    private class IntegerArgumentMarshaller implements ArgumentMarshaller<Integer> {
        private Integer intValue = 0;

        @Override
        public Integer get() {
            return intValue;
        }

        @Override
        public void set(Integer value) {
            this.intValue = value;
        }
    }


}
