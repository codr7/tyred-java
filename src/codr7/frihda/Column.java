package codr7.frihda;

import java.sql.SQLException;
import java.util.stream.Stream;

public interface Column extends TableDefinition {
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

    Column dup(Table table, String name, Stream<Option> options);

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

