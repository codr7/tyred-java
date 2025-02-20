package codr7.tyred;

import java.util.stream.Stream;

public interface Model {
    boolean isModified(Context cx);
    boolean exists(Context cx);
    Record record();
    Stream<Table> tables();
}
