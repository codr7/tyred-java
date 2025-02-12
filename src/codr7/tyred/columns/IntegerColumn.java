package codr7.tyred.columns;

import codr7.tyred.*;

public final class IntegerColumn extends BaseColumn implements TypedColumn<Integer> {
    public IntegerColumn(final Table table, final String name, final Option...options) {
        super(table, name, options);
        table.add(this);
    }

    @Override
    public String columnType() {
        return "INTEGER";
    }

    @Override
    public TableColumn dup(Table table, String name, Option...options) {
        return new IntegerColumn(table, name, options);
    }
}
