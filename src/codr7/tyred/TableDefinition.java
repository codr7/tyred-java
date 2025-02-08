package codr7.tyred;

public interface TableDefinition extends Definition {
    @Override
    default void create(final Context cx) {
        cx.exec("ALTER TABLE " + Utils.quote(table().name()) + " ADD " + createSql());
    }

    @Override
    default void drop(final Context cx) {
        cx.exec("ALTER TABLE " + Utils.quote(table().name()) + " DROP " + definitionType() + ' ' + Utils.quote(name()));
    }

    boolean isNullable();
    boolean isPrimaryKey();

    default String nameSql() {
        return Utils.quote(name());
    }

    Table table();
}
