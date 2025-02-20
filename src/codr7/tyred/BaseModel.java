package codr7.tyred;

public abstract class BaseModel implements Model {
    private final Record record;

    public BaseModel(Record r) {
        this.record = r;
    }

    public boolean isModified(final Context cx) {
        return tables().anyMatch(t -> record.isModified(t, cx));
    }

    public boolean exists(final Context cx) {
        return tables().allMatch(t -> record.exists(t, cx));
    }

    public final Record record() {
        return record;
    }

    public final Model store(final Context cx) {
        tables().forEach(t -> record.store(t, cx));
        return this;
    }
}
