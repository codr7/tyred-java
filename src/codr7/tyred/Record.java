package codr7.tyred;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class Record {
    private final Map<Column, Object> fields = new TreeMap<>();

    public Stream<Map.Entry<Column, Object>> fields() {
        return fields.entrySet().stream();
    }

    public Object get(Column c) {
        return fields.get(c);
    }

    public <T> T get(TypedColumn<T> c) {
        return (T)fields.get(c);
    }

    public void set(Column c, Object v) {
            fields.put(c, v);
    }

    public <T> void set(TypedColumn<T> c, T v) {
        fields.put(c, v);
    }
}