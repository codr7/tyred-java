package codr7.tyred.columns;

import codr7.tyred.*;

import java.time.LocalDateTime;

public final class DateTimeColumn extends BaseColumn implements TypedColumn<LocalDateTime> {
    public DateTimeColumn(final Table table, final String name, final Option...options) {
        super(table, name, options);
        table.add(this);
    }

    @Override
    public String columnType() {
        return "TIMESTAMP";
    }

    @Override
    public Object decode(final Object v) {
        return ((java.sql.Timestamp)v).toLocalDateTime();
    }

    @Override
    public TableColumn dup(Table table, String name, Option...options) {
        return new DateTimeColumn(table, name, options);
    }
}
