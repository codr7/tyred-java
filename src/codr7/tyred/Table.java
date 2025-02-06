package codr7.tyred;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Table extends BaseDefinition implements Definition {
    private final List<TableColumn> columns = new ArrayList<>();
    private final List<ForeignKey> foreignKeys = new ArrayList<>();
    private Key primaryKey;

    public Table(final String name) {
        super(name);
    }

    public final void add(final TableColumn c) {
        columns.add(c);
    }

    public final void add(final ForeignKey k) {
        foreignKeys.add(k);
    }

    @Override
    public final String createSQL() {
        return Definition.super.createSQL() + " (" +
                columns.stream().
                        map(TableColumn::createSQL).
                        collect(Collectors.joining( ", ")) +
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
            q.next();
            return q.getBoolean(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void migrate(final Context cx) {
        if (exists(cx)) {
            for (final var c: columns) {
                c.migrate(cx);
            }

            primaryKey().migrate(cx);

            for (final var k: foreignKeys) {
                k.migrate(cx);
            }
        } else {
            create(cx);
            primaryKey().create(cx);

            for (final var k: foreignKeys) {
                k.create(cx);
            }
        }
    }

    public Key primaryKey() {
        if (primaryKey == null) {
            primaryKey = new Key(
                    this,
                    name() + "Key",
                    columns.stream().filter(TableDefinition::isPrimaryKey));
        }

        return primaryKey;
    }
}
