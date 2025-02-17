package codr7.tyred;

import java.util.HashMap;
import java.util.Map;

public class Transaction {
    public final String savePoint;
    private final Map<Pair<Record, Column>, Object> storedValues = new HashMap<>();

    public Transaction(final String savePoint) {
        this.savePoint = savePoint;
    }

    public void commit(final Context cx) {
        if (savePoint == null) {
            cx.exec("COMMIT");
        } else {
            cx.exec("RELEASE SAVEPOINT " + savePoint);

            for (final var sv: storedValues.entrySet()) {
                cx.storeValue(sv.getKey(), sv.getValue());
            }
        }

        storedValues.clear();
    }

    public void rollback(final Context cx) {
        if (savePoint == null) {
            cx.exec("ROLLBACK");
        } else {
            cx.exec("ROLLBACK TO SAVEPOINT " + savePoint);
        }

        storedValues.clear();
    }

    public void storeValue(final Pair<Record, Column> rc, final Object v) {
        storedValues.put(rc, v);
    }

    public Object storedValue(final Record r, final Column c) {
        return storedValues.get(new Pair<>(r, c));
    }
}
