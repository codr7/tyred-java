package codr7.tyred;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Table extends BaseDefinition implements Definition {
    public interface EventHandler {
        void call(Record r, Context cx);
    }

    private final List<TableColumn> columns = new ArrayList<>();
    private final List<ForeignKey> foreignKeys = new ArrayList<>();
    private Key primaryKey;

    public final List<EventHandler> beforeInsert = new ArrayList<>();
    public final List<EventHandler> afterInsert = new ArrayList<>();
    public final List<EventHandler> beforeUpdate = new ArrayList<>();
    public final List<EventHandler> afterUpdate = new ArrayList<>();

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
                        collect(Collectors.joining(", ")) +
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

    public void insert(final Record r, final Context cx) {
        for (final var h : beforeInsert) {
            h.call(r, cx);
        }

        final var cs = columns.stream().
                filter(c -> r.get(c) != null).
                map(c -> new Pair<Column, Object>(c, r.get(c))).
                toList();

        final var sql = "INSERT INTO " + SQL.quote(name()) + " (" +
                cs.stream().map(cv -> SQL.quote(cv.left().name())).collect(Collectors.joining(", ")) +
                ") VALUES (" +
                String.join(", ", Collections.nCopies(cs.size(), "?")) +
                ')';

        cx.exec(sql, cs.stream().map(Pair::right).toArray(Object[]::new));

        for (final var h : afterInsert) {
            h.call(r, cx);
        }

        for (final var cv: cs) {
            cx.storeValue(r, cv.left(), cv.right());
        }
    }

    @Override
    public void migrate(final Context cx) {
        if (exists(cx)) {
            for (final var c : columns) {
                c.migrate(cx);
            }

            primaryKey().migrate(cx);

            for (final var k : foreignKeys) {
                k.migrate(cx);
            }
        } else {
            create(cx);
            primaryKey().create(cx);

            for (final var k : foreignKeys) {
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

    public boolean update(final Record r, final Context cx) {
        for (final var h : beforeUpdate) {
            h.call(r, cx);
        }

        final var cs = columns.stream().
                filter(c -> r.get(c) != null).
                map(c -> new Pair<Column, Object>(c, r.get(c))).
                filter(cv -> {
                    final var sv = cx.storedValue(r, cv.left());
                    return sv == null && !cv.right().equals(sv);
                }).toList();

        if (cs.isEmpty()) {
            return false;
        }

        final var kcs = primaryKey().columns().
                map(c -> new Pair<Column, Object>(c, cx.storedValue(r, c))).
                toList();

        final var sql = "UPDATE " + SQL.quote(name()) + " SET " +
                cs.stream().
                        map(cv -> SQL.quote(cv.left().name()) + "= ?").
                        collect(Collectors.joining(", ")) +
                " WHERE ";

        cx.exec(sql, Stream.concat(cs.stream().map(Pair::right), kcs.stream().map(Pair::right)).toArray(Object[]::new));

        for (final var h : afterUpdate) {
            h.call(r, cx);
        }

        for (final var cv: cs) {
            cx.storeValue(r, cv.left(), cv.right());
        }

        return true;
    }
}
