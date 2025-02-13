package codr7.tyred;

public abstract class BaseModel implements Model {
    private final Record record;

    public BaseModel(Record r) {
        this.record = r;
    }

    public boolean isModified(final Context cx) {
        for (final var t: tables()) {
            if (record.isModified(t, cx)) {
                return true;
            }
        }

        return false;
    }

    public boolean isStored(final Context cx) {
        for (final var t: tables()) {
            if (!record.isStored(t, cx)) {
                return false;
            }
        }

        return true;
    }

    public final Record record() {
        return record;
    }

    public final Model store(final Context cx) {
        for (final var t: tables()) {
            record.store(t, cx);
        }

        return this;
    }
}
