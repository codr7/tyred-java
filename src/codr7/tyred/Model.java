package codr7.tyred;

public interface Model {
    boolean isModified(Context cx);
    boolean isStored(Context cx);
    Record record();
    Table[] tables();
}
