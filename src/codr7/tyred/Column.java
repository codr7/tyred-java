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

    default Object decode(Object value) {
        return value;
    }

    default Object encode(Object value) {
        return value;
    }

    default boolean equal(final Object l, final Object r) {
        return l.equals(r);
    }

    default Condition EQ(final Object value) {
        return new Condition(nameSql() + " = ?", value);
    }
    default Condition GT(final Object value) {
        return new Condition(nameSql() + " > ?", value);
    }
    default Condition LT(final Object value) {
        return new Condition(nameSql() + " < ?", value);
    }
}
