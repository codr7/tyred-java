package codr7.tyred;

import java.util.stream.Stream;

public record Join(Source left, Source right, Condition on) implements Source {
    @Override
    public Stream<Object> sourceParams() {
        return Stream.concat(left.sourceParams(),
                Stream.concat(right.sourceParams(), on.params()));
    }

    @Override
    public String sourceSql() {
        return left.sourceSql() + " JOIN " + right.sourceSql() + " ON " + on.sql();
    }
}
