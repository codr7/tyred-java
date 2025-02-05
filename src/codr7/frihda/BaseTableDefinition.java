package codr7.frihda;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BaseTableDefinition extends BaseDefinition {
    private final Set<Column.Option> options;
    private final Table table;

    public BaseTableDefinition(final Table table, final String name, Column.Option...options) {
        super(name);
        this.table = table;
        this.options = Set.of(options);
    }

    public boolean isNullable() {
        return options.contains(TableDefinition.Option.Nullable);
    }

    public boolean isPrimaryKey() {
        return options.contains(TableDefinition.Option.PrimaryKey);
    }

    public Stream<TableDefinition.Option> options() {
        return options.stream();
    }

    public Table table() {
        return table;
    }
}
