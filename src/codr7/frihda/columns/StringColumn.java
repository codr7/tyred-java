package codr7.frihda.columns;

import codr7.frihda.BaseColumn;
import codr7.frihda.Column;
import codr7.frihda.Table;
import codr7.frihda.TypedColumn;

import java.util.stream.Stream;

public final class StringColumn extends BaseColumn implements TypedColumn<String> {
    private final int size;

    public StringColumn(final Table table, final String name, final int size, final Stream<Option> options) {
        super(table, name, options);
        table.add(this);
        this.size = size;
    }

    @Override
    public String columnType() {
        return "VARCHAR(" + size + ')';
    }

    @Override
    public Column dup(Table table, String name, Stream<Option> options) {
        return new StringColumn(table, name, size, options);
    }
}
