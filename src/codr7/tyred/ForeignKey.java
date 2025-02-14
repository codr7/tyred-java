package codr7.tyred;

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

    public static String toSql(Action a) {
        return switch (a) {
            case Cascade -> "CASCADE";
            case NoAction -> "NO ACTION";
            case Restrict -> "RESTRICT";
            case SetDefault -> "SET DEFAULT";
            case SetNull -> "SET NULL";
        };
    }

    private final List<Pair<TableColumn, TableColumn>> foreignColumns = new ArrayList<>();
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
            final var c = fc.dup(table, name + Utils.toNameCase(fc.name()), options().toArray(Option[]::new));
            add(c);
            foreignColumns.add(new Pair<>(c, fc));
        });
    }

    @Override
    public String constraintType() {
        return "FOREIGN KEY";
    }

    @Override
    public String createSql() {
        return super.createSql() + " REFERENCES " +
                Utils.quote(foreignTable.name()) + " (" +
                foreignColumns.stream().
                        map(c -> Utils.quote(c.right().name())).
                        collect(Collectors.joining( ", ")) +
                ") ON DELETE " + toSql(onDelete) + " ON UPDATE " + toSql(onUpdate);
    }

    public Stream<Pair<TableColumn, TableColumn>> foreignColumns() {
        return foreignColumns.stream();
    }

    public Table foreignTable() {
        return foreignTable;
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
