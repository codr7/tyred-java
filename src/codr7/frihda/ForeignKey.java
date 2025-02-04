package codr7.frihda;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ForeignKey extends BaseConstraint implements Constraint {
    private final List<Column> foreignColumns = new ArrayList<>();
    private final Table foreignTable;

    public ForeignKey(final Table table, final String name, final Table foreignTable, final Stream<Option> options) {
        super(table, name, Stream.empty(), options);
        this.foreignTable = foreignTable;
        table.add(this);

        foreignTable.primaryKey().columns().forEach(fc -> {
            foreignColumns.add(fc);
            final var c = fc.dup(table, name + StringUtils.toNameCase(fc.name()), options());
            add(c);
        });
    }

    @Override
    public String constraintType() {
        return "FOREIGN KEY";
    }

    @Override
    public String createSQL() {
        return super.createSQL() + " REFERENCES " +
                SQL.quote(foreignTable.name()) + " (" +
                foreignColumns.stream().
                        map(c -> SQL.quote(c.name())).
                        collect(Collectors.joining( ", ")) +
                ')';
    }
}
