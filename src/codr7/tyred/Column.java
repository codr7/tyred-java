package codr7.tyred;

import java.util.stream.Stream;

public interface Column extends Comparable<Column> {
    String nameSql();

    default Stream<Object> columnParams() {
        return Stream.empty();
    }

    default String columnSql() {
        return nameSql();
    }

    default boolean equal(final Object l, final Object r) {
        return l.equals(r);
    }

    default Condition EQ(final Object value) {
        return new Condition(nameSql() + " = ?", value);
    }
}
