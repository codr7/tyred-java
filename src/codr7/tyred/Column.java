package codr7.tyred;

public interface Column extends Comparable<Column> {
    String nameSql();

    default Condition EQ(final Object value) {
        return new Condition(nameSql() + " = ?", value);
    }
}
