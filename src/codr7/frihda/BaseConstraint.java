package codr7.frihda;

import java.util.List;
import java.util.stream.Stream;

public abstract class BaseConstraint extends BaseTableDefinition implements Constraint {
    private final List<Column> columns;

    public BaseConstraint(final Table table,
                          final String name,
                          final Stream<Column> columns,
                          final Stream<Option> options) {
        super(table, name, options);
        this.columns = columns.toList();
    }

    @Override
    public Stream<Column> columns() { return columns.stream(); }
}
