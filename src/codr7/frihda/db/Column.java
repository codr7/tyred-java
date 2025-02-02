package codr7.frihda.db;

import java.sql.SQLException;

public interface Column extends TableDefinition {
    String columnType();

    @Override
    default String definitionType() {
        return "COLUMN";
    }

    @Override
    default boolean exists(final Context cx) {
        try (final var q = cx.query("""
                SELECT EXISTS
                (SELECT FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = ? AND column_name = ?)
                """, table().name(), name())) {
            return q.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
