package codr7.tyred;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Table extends BaseDefinition implements Definition, Source {
    public interface EventHandler {
        void call(Record r, Context cx);
    }

    private final List<TableColumn> columns = new ArrayList<>();
    private final List<ForeignKey> foreignKeys = new ArrayList<>();
    private final List<Index> indexes = new ArrayList<>();

    private Key primaryKey;

    public final List<EventHandler> beforeInsert = new ArrayList<>();
    public final List<EventHandler> afterInsert = new ArrayList<>();
    public final List<EventHandler> beforeUpdate = new ArrayList<>();
    public final List<EventHandler> afterUpdate = new ArrayList<>();

    public Table(final String name) {
        super(name);
    }

    public final void add(final ForeignKey k) {
        foreignKeys.add(k);
    }

    public final void add(final Index i) {
        indexes.add(i);
    }

    public final void add(final TableColumn c) {
        columns.add(c);
    }

    public final Stream<TableColumn> columns() {
        return columns.stream();
    }

    @Override
    public final String createSql() {
        return super.createSql() + " (" +
                columns.stream().
                        map(TableColumn::createSql).
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
                filter(c -> r.getObject(c) != null).
                map(c -> new Pair<Column, Object>(c, r.getObject(c))).
                toList();

        final var sql = "INSERT INTO " + Utils.quote(name()) + " (" +
                cs.stream().map(cv -> cv.left().nameSql()).collect(Collectors.joining(", ")) +
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
        } else {
            create(cx);
        }

        primaryKey().migrate(cx);

        for (final var fk : foreignKeys) {
            fk.migrate(cx);
        }

        for (final var i : indexes) {
            i.migrate(cx);
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

    @Override
    public Stream<Object> sourceParams() {
        return Stream.empty();
    }

    @Override
    public String sourceSql() {
        return Utils.quote(name());
    }

    public boolean update(final Record r, final Context cx) {
        for (final var h : beforeUpdate) {
            h.call(r, cx);
        }

        final var cs = columns.stream().
                filter(c -> r.getObject(c) != null).
                map(c -> new Pair<Column, Object>(c, r.getObject(c))).
                filter(cv -> {
                    final var sv = cx.storedValue(r, cv.left());
                    return !cv.right().equals(sv);
                }).toList();

        if (cs.isEmpty()) {
            return false;
        }

        final var kcs = primaryKey().columns().
                map(c -> new Pair<Column, Object>(c, cx.storedValue(r, c))).
                toList();

        final var wc = Condition.AND(kcs.stream().map(cv -> {
            final var v = cv.right();

            if (v == null) {
                throw new RuntimeException("Missing key: " + cv.left());
            }

            return cv.left().EQ(cv.right());
        }).toArray(Condition[]::new));

        final var sql = "UPDATE " + Utils.quote(name()) + " SET " +
                cs.stream().
                        map(cv -> cv.left().nameSql() + "= ?").
                        collect(Collectors.joining(", ")) +
                " WHERE " + wc.sql();

        final var ps = Stream.concat(cs.stream().map(Pair::right), wc.params()).toArray(Object[]::new);
        cx.exec(sql, ps);

        for (final var h : afterUpdate) {
            h.call(r, cx);
        }

        for (final var cv: cs) {
            cx.storeValue(r, cv.left(), cv.right());
        }

        return true;
    }
}
