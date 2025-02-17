package codr7.tyred;

import java.util.stream.Stream;

public class Key extends BaseConstraint implements Constraint {
    public Key(final Table table, final String name, final Stream<TableColumn> columns, final Option...options) {
        super(table, name, columns, options);
    }

    public Condition condition(final Record r) {
        final var cs = columns().
                map(c -> new Pair<Column, Object>(c, c.encode(r.getObject((c))))).
                toList();

        return Condition.AND(cs.stream().map(cv -> {
            final var v = cv.right();

            if (v == null) {
                throw new RuntimeException("Missing key: " + cv.left());
            }

            return cv.left().EQ(cv.right());
        }).toArray(Condition[]::new));
    }

    @Override
    public String constraintType() {
        return (this == table().primaryKey()) ? "PRIMARY KEY" : "UNIQUE";
    }
}
