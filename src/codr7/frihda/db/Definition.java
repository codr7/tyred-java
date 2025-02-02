package codr7.frihda.db;

public interface Definition {
    default void create(final Context cx) {
        cx.exec(createSQL());
    }

    default String createSQL() {
        return "CREATE " + definitionType() + SQL.quote(name());
    }

    String definitionType();

    default void drop(final Context cx) {
        cx.exec(dropSQL());
    }

    default String dropSQL() {
        return "DROP " + definitionType() + SQL.quote(name());
    }

    boolean exists(Context cx);
    String name();
}
