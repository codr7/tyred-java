package codr7.tyred;

public abstract class BaseModel implements Model {
    private final Context context;
    private final Record record;

    public BaseModel(Context cx, Record r) {
        this.context = cx;
        this.record = r;
    }

    public final Context context() {
        return context;
    }

    public boolean isModified() {
        for (final var t: tables()) {
            if (record.isModified(t, context)) {
                return true;
            }
        }

        return false;
    }

    public boolean isStored() {
        for (final var t: tables()) {
            if (!record.isStored(t, context)) {
                return false;
            }
        }

        return true;
    }

    public final Record record() {
        return record;
    }
}
