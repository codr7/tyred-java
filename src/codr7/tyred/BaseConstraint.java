package codr7.tyred;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Stream;

public abstract class BaseConstraint extends BaseTableDefinition implements Constraint {
    private final List<TableColumn> columns;

    public BaseConstraint(final Table table,
                          final String name,
                          final Stream<TableColumn> columns,
                          final Option...options) {
        super(table, name, options);
        this.columns = new ArrayList<>(columns.toList());
    }

    public void add(final TableColumn c) {
        columns.add(c);
    }

    @Override
    public Stream<TableColumn> columns() { return columns.stream(); }
}
