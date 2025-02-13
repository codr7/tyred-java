package codr7.tyred;

import java.util.stream.Stream;

public interface Source {
    Stream<Object> sourceParams();
    String sourceSql();
}
