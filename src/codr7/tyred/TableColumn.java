package codr7.tyred;

import java.sql.SQLException;

public interface TableColumn extends Column, TableDefinition {
    String columnType();

    @Override
    default String createSQL() {
        var sql = SQL.quote(name()) + ' ' + columnType();
        if (!isNullable()) { sql += " NOT NULL"; }
        return sql;
    }

    @Override
    default String definitionType() {
        return "COLUMN";
    }

    TableColumn dup(Table table, String name, Option...options);

    @Override
    default boolean exists(final Context cx) {
        try (final var q = cx.query("""
                SELECT EXISTS
                (SELECT FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = ? AND column_name = ?)
                """, table().name(), name())) {
            q.next();
            return q.getBoolean(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

