package codr7.frihda.db;

public class BaseTableDefinition extends BaseDefinition {
    private Table table;

    public BaseTableDefinition(final Table table, final String name) {
        super(name);
    }

    public Table table() {
        return table;
    }
}
