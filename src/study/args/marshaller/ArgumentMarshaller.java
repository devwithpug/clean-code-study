package study.args.marshaller;

import study.args.exception.ArgsException;

import java.util.Iterator;

public interface ArgumentMarshaller<T> {
    T get();
    void set(Iterator<String> currentArgument) throws ArgsException;
}
