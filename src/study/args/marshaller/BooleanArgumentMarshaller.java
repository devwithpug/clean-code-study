package study.args.marshaller;

import study.args.exception.ArgsException;

import java.util.Iterator;

public class BooleanArgumentMarshaller implements ArgumentMarshaller<Boolean> {

    private boolean booleanValue = false;

    @Override
    public Boolean get() {
        return booleanValue;
    }

    @Override
    public void set(Iterator<String> currentArgument) throws ArgsException {
        booleanValue = true;
    }

}
