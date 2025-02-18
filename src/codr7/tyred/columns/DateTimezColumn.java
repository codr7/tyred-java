package codr7.tyred.columns;

import codr7.tyred.*;

import java.time.OffsetDateTime;

public final class DateTimezColumn extends BaseColumn implements TypedColumn<OffsetDateTime> {
    public DateTimezColumn(final Table table, final String name, final Option...options) {
        super(table, name, options);
        table.add(this);
    }

    @Override
    public String columnType() {
        return "TIMESTAMP(9) WITH TIME ZONE";
    }

    @Override
    public TableColumn dup(Table table, String name, Option...options) {
        return new DateTimezColumn(table, name, options);
    }
}
