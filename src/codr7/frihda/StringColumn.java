package codr7.frihda;

import java.util.stream.Stream;

public class StringColumn extends BaseColumn implements TypedColumn<String> {
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
}
