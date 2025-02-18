package codr7.tyred.columns;

import codr7.tyred.*;

import java.time.OffsetTime;

public final class TimezColumn extends BaseColumn implements TypedColumn<OffsetTime> {
    public TimezColumn(final Table table, final String name, final Option...options) {
        super(table, name, options);
        table.add(this);
    }

    @Override
    public String columnType() {
        return "TIME(9) WITH TIME ZONE";
    }

    @Override
    public TableColumn dup(Table table, String name, Option...options) {
        return new TimezColumn(table, name, options);
    }
}
