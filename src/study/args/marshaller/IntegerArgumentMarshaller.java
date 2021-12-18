package study.args.marshaller;

import study.args.exception.ArgsException;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class IntegerArgumentMarshaller implements ArgumentMarshaller<Integer> {

    private Integer intValue = 0;

    @Override
    public Integer get() {
        return intValue;
    }

    @Override
    public void set(Iterator<String> currentArgument) throws ArgsException {
        String parameter = null;
        try {
            parameter = currentArgument.next();
            this.intValue = Integer.parseInt(parameter);
        } catch (NoSuchElementException e) {
            throw new ArgsException(ArgsException.ErrorCode.MISSING_INTEGER);
        } catch (NumberFormatException e) {
            throw new ArgsException(ArgsException.ErrorCode.INVALID_INTEGER, parameter);
        }
    }

}
