package codr7.frihda;

import java.util.stream.Stream;

public class BaseColumn extends BaseTableDefinition {
    public BaseColumn(final Table table, final String name, final Column.Option...options) {
        super(table, name, options);
    }
}
