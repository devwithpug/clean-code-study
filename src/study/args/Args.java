package study.args;

import study.args.exception.ArgsException;
import study.args.exception.ArgsException.ErrorCode;
import study.args.marshaller.*;

import java.util.*;

/**
 * 리팩터링 후
 */

public class Args {

    private String schema;
    private Map<Character, ArgumentMarshaller> marshaller = new HashMap<>();
    private Set<Character> argsFound = new HashSet<>();
    private Iterator<String> currentArgument;
    private List<String> argsList;

    public Args(String schema, String[] args) throws ArgsException {
        this.schema = schema;
        argsList = Arrays.asList(args);
        parse();
    }

    private void parse() throws ArgsException {
        parseSchema();
        parseArguments();
    }

    private boolean parseSchema() throws ArgsException {
        for (String element : schema.split(",")) {
            if (element.length() > 0) {
                String trimmedElement = element.trim();
                parseSchemaElement(trimmedElement);
            }
        }
        return true;
    }

    private void parseSchemaElement(String element) throws ArgsException {
        char elementId = element.charAt(0);
        String elementTail = element.substring(1);
        validateSchemaElementId(elementId);
        if (isBooleanSchemaElement(elementTail)) {
            marshaller.put(elementId, new BooleanArgumentMarshaller());
        } else if (isStringSchemaElement(elementTail)) {
            marshaller.put(elementId, new StringArgumentMarshaller());
        } else if (isIntegerSchemaElement(elementTail)) {
            marshaller.put(elementId, new IntegerArgumentMarshaller());
        } else if (isDoubleSchemaElement(elementTail)) {
            marshaller.put(elementId, new DoubleArgumentMarshaller());
        } else {
            throw new ArgsException(ErrorCode.INVALID_FORMAT, elementId, elementTail);
        }
    }

    private void validateSchemaElementId(char elementId) throws ArgsException {
        if (!Character.isLetter(elementId)) {
            throw new ArgsException(ErrorCode.INVALID_ARGUMENT_NAME, elementId, null);
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
    private boolean isDoubleSchemaElement(String elementTail) {
        return elementTail.equals("##");
    }

    private boolean parseArguments() throws ArgsException {
        for (currentArgument = argsList.iterator(); currentArgument.hasNext();) {
            String arg = currentArgument.next();
            parseArgument(arg);
        }
        return true;
    }

    private void parseArgument(String arg) throws ArgsException {
        if (arg.startsWith("-")) {
            parseElements(arg);
        }
    }

    private void parseElements(String arg) throws ArgsException {
        for (int i = 1; i < arg.length(); i++) {
            parseElement(arg.charAt(i));
        }
    }

    private void parseElement(char argChar) throws ArgsException {
        if (setArgument(argChar)) {
            argsFound.add(argChar);
        } else {
            throw new ArgsException(ErrorCode.UNEXPECTED_ARGUMENT, argChar, null);
        }
    }

    private boolean setArgument(char argChar) throws ArgsException {
        ArgumentMarshaller m = marshaller.get(argChar);
        if (m == null) {
            return false;
        }
        try {
            m.set(currentArgument);
            return true;
        } catch (ArgsException e) {
            e.setErrorArgumentId(argChar);
            throw e;
        }
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

    public double getDouble(char arg) {
        ArgumentMarshaller<Double> am = marshaller.get(arg);
        return am == null ? 0.0 : am.get();
    }

    public boolean has(char arg) {
        return argsFound.contains(arg);
    }

}
