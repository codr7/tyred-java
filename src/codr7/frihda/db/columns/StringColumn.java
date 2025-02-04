package codr7.frihda.db.columns;

import codr7.frihda.db.BaseColumn;
import codr7.frihda.db.Table;
import codr7.frihda.db.TypedColumn;

public class StringColumn extends BaseColumn implements TypedColumn<String> {
    private final int size;

    public StringColumn(Table table, String name, int size) {
        super(table, name);
        table.add(this);
        this.size = size;
    }

    @Override
    public String columnType() {
        return "VARCHAR(" + size + ')';
    }
}
