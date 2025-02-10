package codr7.tyred;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

public class Record {
    private final Map<Column, Object> fields = new TreeMap<>();

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

    public Object getObject(Column c) {
        return fields.get(c);
    }

    public <T> T get(TypedColumn<T> c) {
        return (T)fields.get(c);
    }

    public void setObject(Column c, Object v) {
            fields.put(c, v);
    }

    public <T> void set(TypedColumn<T> c, T v) {
        fields.put(c, v);
    }
}