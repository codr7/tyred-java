package codr7.tyred;

public abstract class BaseColumn extends BaseTableDefinition {
    public BaseColumn(final Table table, final String name, final Option...options) {
        super(table, name, options);
    }
}
