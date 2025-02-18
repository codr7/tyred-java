package codr7.tyred.columns;

import codr7.tyred.*;
import java.time.LocalDate;

public final class DateColumn extends BaseColumn implements TypedColumn<LocalDate> {
    public DateColumn(final Table table, final String name, final Option...options) {
        super(table, name, options);
        table.add(this);
    }

    @Override
    public String columnType() {
        return "DATE";
    }

    @Override
    public Object decode(final Object v) {
        return ((java.sql.Date)v).toLocalDate();
    }

    @Override
    public TableColumn dup(Table table, String name, Option...options) {
        return new DateColumn(table, name, options);
    }
}
