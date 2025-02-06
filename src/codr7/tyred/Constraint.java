package codr7.tyred;

import java.sql.SQLException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Constraint extends TableDefinition {
    Stream<TableColumn> columns();
    String constraintType();

    @Override
    default String definitionType() {
        return "CONSTRAINT";
    }

    @Override
    default String createSQL() {
        return TableDefinition.super.createSQL() + ' ' +
                constraintType() + " (" +
                columns().
                        map(c -> SQL.quote(c.name())).
                        collect(Collectors.joining( ", ")) +
                ')';
    }

    @Override
    default boolean exists(final Context cx) {
        try (final var q = cx.query("""
                SELECT EXISTS
                (SELECT FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS WHERE table_name = ? AND constraint_name = ?)
                """, table().name(), name())) {
            q.next();
            return q.getBoolean(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
