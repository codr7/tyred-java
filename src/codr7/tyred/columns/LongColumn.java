package codr7.tyred.columns;

import codr7.tyred.*;

public final class LongColumn extends BaseColumn implements TypedColumn<Long> {
    public LongColumn(final Table table, final String name, final Option...options) {
        super(table, name, options);
        table.add(this);
    }

    @Override
    public String columnType() {
        return "BIGINT";
    }

    @Override
    public TableColumn dup(Table table, String name, Option...options) {
        return new LongColumn(table, name, options);
    }
}
