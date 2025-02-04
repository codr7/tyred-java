package codr7.frihda;

import java.util.stream.Stream;

public class Key extends BaseConstraint implements Constraint {
    public Key(final Table table, final String name, final Stream<Column> columns, final Stream<Option> options) {
        super(table, name, columns, options);
    }

    @Override
    public String constraintType() {
        return (this == table().primaryKey()) ? "PRIMARY KEY" : "UNIQUE";
    }
}
