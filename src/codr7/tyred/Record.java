package codr7.tyred;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

public final class Record {
    private final Map<Column, Object> fields = new TreeMap<>();

    public Record() {}

    public Record(final ResultSet source, final Stream<Column> columns) {
        var i = 1;

        for (final var c: columns.toArray(Column[]::new)) {
            try {
                setObject(c, c.decode(source.getObject(i)));
                i++;
            } catch (final SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof Record r) {
            if (r.fields.size() != fields.size()) {
                return false;
            }

            final var i1 = fields.entrySet().iterator();
            final var i2 = r.fields.entrySet().iterator();

            while (i1.hasNext()) {
                final var f1 = i1.next();
                final var f2 = i2.next();
                final var c = f1.getKey();

                if (c != f2.getKey()) {
                    return false;
                }

                if (!c.equal(f1.getValue(), f2.getValue())) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    public Stream<Map.Entry<Column, Object>> fields() {
        return fields.entrySet().stream();
    }

    public Record get(final ForeignKey k) {
        final var r = new Record();

        k.foreignColumns().forEach(c -> {
            r.setObject(c.right(), getObject(c.left()));
        });

        return r;
    }

    public <T> T get(final TypedColumn<T> c) {
        return (T)fields.get(c);
    }

    public Object getObject(final Column c) {
        return fields.get(c);
    }

    public boolean isModified(final Table t, final Context cx) {
        return t.columns().anyMatch(c -> {
            final var v = getObject(c);
            final var sv = cx.storedValue(this, c);

            if (v == null && sv == null) {
                return false;
            }

            if (v == null || sv == null) {
                return true;
            }

            return !c.equal(v, sv);
        });
    }

    public boolean isStored(final Table t, final Context cx) {
        return t.primaryKey().columns().allMatch(c -> cx.storedValue(this, c) != null);
    }

    public <T> Record set(final TypedColumn<T> c, T v) {
        fields.put(c, v);
        return this;
    }

    public <T> Record set(final ForeignKey k, final Record v) {
        k.foreignColumns().forEach(c -> {
            setObject(c.left(), v.getObject(c.right()));
        });

        return this;
    }

    public Record setObject(final Column c, final Object v) {
        fields.put(c, v);
        return this;
    }

    public Record store(final Table t, final Context cx) {
        if (isStored(t, cx)) {
            t.update(this, cx);
        } else {
            t.insert(this, cx);
        }

        return this;
    }
}