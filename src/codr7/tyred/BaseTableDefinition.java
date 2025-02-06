package codr7.tyred;

import java.util.Set;
import java.util.stream.Stream;

public class BaseTableDefinition extends BaseDefinition {
    private final Set<Option> options;
    private final Table table;

    public BaseTableDefinition(final Table table, final String name, Option...options) {
        super(name);
        this.table = table;
        this.options = Set.of(options);
    }

    public boolean isNullable() {
        return options.contains(Option.Nullable);
    }

    public boolean isPrimaryKey() {
        return options.contains(Option.PrimaryKey);
    }

    public Stream<Option> options() {
        return options.stream();
    }

    public Table table() {
        return table;
    }
}
