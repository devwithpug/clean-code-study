package study.args.marshaller;

import study.args.exception.ArgsException;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class DoubleArgumentMarshaller implements ArgumentMarshaller<Double> {

    private double doubleValue = 0;

    @Override
    public Double get() {
        return doubleValue;
    }

    @Override
    public void set(Iterator<String> currentArgument) throws ArgsException {
        String parameter = null;
        try {
            parameter = currentArgument.next();
            this.doubleValue = Double.parseDouble(parameter);
        } catch (NoSuchElementException e) {
            throw new ArgsException(ArgsException.ErrorCode.MISSING_DOUBLE);
        } catch (NumberFormatException e) {
            throw new ArgsException(ArgsException.ErrorCode.INVALID_DOUBLE, parameter);
        }
    }

}
