package codr7.tyred;

public interface Definition {
    default void create(final Context cx) {
        cx.exec("CREATE " + createSql());
    }

    default String createSql() {
        return definitionType() + ' ' + Utils.quote(name());
    }

    String definitionType();

    default void drop(final Context cx) {
        cx.exec("DROP " + definitionType() + ' ' + Utils.quote(name()));
    }

    boolean exists(Context cx);

    default void migrate(final Context cx) {
        if (!exists(cx)) { create(cx); }
    }

    String name();
}
