package codr7.frihda;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ForeignKey extends BaseConstraint implements Constraint {
    public enum Action {
        Cascade,
        NoAction,
        Restrict,
        SetDefault,
        SetNull
    }

    public static String toSQL(Action a) {
        return switch (a) {
            case Cascade -> "CASCADE";
            case NoAction -> "NO ACTION";
            case Restrict -> "RESTRICT";
            case SetDefault -> "SET DEFAULT";
            case SetNull -> "SET NULL";
        };
    }

    private final List<Column> foreignColumns = new ArrayList<>();
    private final Table foreignTable;

    private Action onDelete = Action.Restrict;
    private Action onUpdate = Action.Cascade;

    public ForeignKey(final Table table,
                      final String name,
                      final Table foreignTable,
                      final Option...options) {
        super(table, name, Stream.empty(), options);
        this.foreignTable = foreignTable;
        table.add(this);

        foreignTable.primaryKey().columns().forEach(fc -> {
            foreignColumns.add(fc);
            final var c = fc.dup(table, name + StringUtils.toNameCase(fc.name()), options().toArray(Option[]::new));
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
                ") ON DELETE " + toSQL(onDelete) + " ON UPDATE " + toSQL(onUpdate);
    }

    public ForeignKey onDelete(final Action action) {
        onDelete = action;
        return this;
    }

    public ForeignKey onUpdate(final Action action) {
        onUpdate = action;
        return this;
    }
}
