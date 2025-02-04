package codr7.frihda.db;

public interface TableDefinition extends Definition {
    Table table();

    @Override
    default void create(final Context cx) {
        cx.exec("ALTER TABLE " + SQL.quote(table().name()) + " ADD " + createSQL());
    }

    @Override
    default void drop(final Context cx) {
        cx.exec("ALTER TABLE " + SQL.quote(table().name()) + " DROP " + definitionType() + ' ' + SQL.quote(name()));
    }
}
