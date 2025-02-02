package codr7.frihda.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Table extends BaseDefinition implements Definition {
    private final List<Column> columns = new ArrayList<>();

    public Table(final String name) {
        super(name);
    }

    @Override
    public final String createSQL() {
        return Definition.super.createSQL() + " (" +
                columns.stream().
                        map(c -> c.name() + ' ' + c.columnType()).
                        collect(Collectors.joining( " ")) +
                ')';
    }

    @Override
    public final String definitionType() {
        return "TABLE";
    }

    @Override
    public boolean exists(final Context cx) {
        try (final var q = cx.query("""
                SELECT EXISTS
                (SELECT FROM INFORMATION_SCHEMA.TABLES WHERE table_name = ?)
                """, name())) {
            return q.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
