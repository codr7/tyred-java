package codr7.tyred.columns;

import codr7.tyred.*;

import java.time.LocalTime;

public final class TimeColumn extends BaseColumn implements TypedColumn<LocalTime> {
    public TimeColumn(final Table table, final String name, final Option...options) {
        super(table, name, options);
        table.add(this);
    }

    @Override
    public String columnType() {
        return "TIME";
    }

    @Override
    public TableColumn dup(Table table, String name, Option...options) {
        return new TimeColumn(table, name, options);
    }
}
