package codr7.tyred;

public interface Column extends Comparable<Column> {
    String nameSql();

    default boolean equal(final Object l, final Object r) {
        return l.equals(r);
    }

    default Condition EQ(final Object value) {
        return new Condition(nameSql() + " = ?", value);
    }
}
