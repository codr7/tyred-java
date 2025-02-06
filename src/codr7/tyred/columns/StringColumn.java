package codr7.tyred.columns;

import codr7.tyred.*;

public final class StringColumn extends BaseColumn implements TypedColumn<String> {
    private final int size;

    public StringColumn(final Table table, final String name, final int size, final Option...options) {
        super(table, name, options);
        table.add(this);
        this.size = size;
    }

    @Override
    public String columnType() {
        return "VARCHAR(" + size + ')';
    }

    @Override
    public TableColumn dup(Table table, String name, Option...options) {
        return new StringColumn(table, name, size, options);
    }
}
